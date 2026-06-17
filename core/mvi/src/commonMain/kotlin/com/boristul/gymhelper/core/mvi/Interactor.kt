package com.boristul.gymhelper.core.mvi

import com.boristul.gymhelper.core.coroutines.AppDispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

interface MviIntent

interface MviEffect

interface InteractorJob

typealias Reducer<Intent, State> = (intent: Intent, state: State) -> State

interface Interactor<State, Intent : MviIntent, Effect : MviEffect> : CoroutineScope {
    val state: StateFlow<State>
    val effects: Flow<Effect>

    fun start()

    fun emit(intent: Intent)

    fun sendEffect(effect: Effect)

    fun close()

    abstract class Abstract<State : Any, Intent : MviIntent, Effect : MviEffect>(
        initialState: State,
        private val reducer: Reducer<Intent, State>,
        dispatchers: AppDispatchers,
        private val initIntents: List<Intent> = emptyList(),
    ) : Interactor<State, Intent, Effect> {
        private val job = SupervisorJob()
        private val intents = Channel<Intent>(Channel.BUFFERED)
        private val mutableState = MutableStateFlow(initialState)
        private val mutableEffects = Channel<Effect>(Channel.BUFFERED)
        private val interactorJobs = mutableMapOf<InteractorJob, Job>()
        private val mutex = Mutex()
        private var isStarted = false

        private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }

        override val coroutineContext: CoroutineContext = job + dispatchers.default + exceptionHandler
        override val state: StateFlow<State> = mutableState.asStateFlow()
        override val effects: Flow<Effect> = mutableEffects.receiveAsFlow()

        protected abstract suspend fun handleIntent(intent: Intent)

        protected open fun initialBind() = Unit

        protected open fun onError(throwable: Throwable) = Unit

        override fun start() {
            if (isStarted) return
            isStarted = true

            intents
                .receiveAsFlow()
                .onEach { intent ->
                    apply(intent)
                    handleIntent(intent)
                }
                .launchIn(this)

            initialBind()
            initIntents.forEach(::emit)
        }

        override fun emit(intent: Intent) {
            launch {
                intents.send(intent)
            }
        }

        override fun sendEffect(effect: Effect) {
            launch {
                mutableEffects.send(effect)
            }
        }

        override fun close() {
            cancel()
        }

        private suspend fun apply(intent: Intent) {
            mutex.withLock {
                mutableState.emit(reducer(intent, mutableState.value))
            }
        }

        protected fun <T> Flow<T>.launchToIntent(mapper: (T) -> Intent) {
            onEach { value -> emit(mapper(value)) }
                .launchIn(this@Abstract)
        }

        protected fun <T> Flow<T>.launchCollect(block: suspend (T) -> Unit) {
            onEach(block)
                .launchIn(this@Abstract)
        }

        protected fun launchManaged(
            key: InteractorJob,
            cancelPrevious: Boolean = true,
            block: suspend () -> Unit,
        ): Job? {
            val launchedJob = synchronized(interactorJobs) {
                val currentJob = interactorJobs[key]
                if (cancelPrevious) {
                    currentJob?.cancel()
                } else if (currentJob?.isActive == true) {
                    return null
                }

                launch(start = CoroutineStart.LAZY) {
                    try {
                        block()
                    } catch (throwable: Throwable) {
                        if (throwable is CancellationException) throw throwable
                        onError(throwable)
                    }
                }.also { nextJob ->
                    nextJob.invokeOnCompletion {
                        synchronized(interactorJobs) {
                            if (interactorJobs[key] == nextJob) {
                                interactorJobs.remove(key)
                            }
                        }
                    }
                    interactorJobs[key] = nextJob
                }
            }

            launchedJob.start()
            return launchedJob
        }
    }
}
