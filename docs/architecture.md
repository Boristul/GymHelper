# GymHelper Architecture

## Product direction

GymHelper starts as a local workout log: open today's training, add workout stages, record sets with weight and reps, and keep results ready for future history and progression features.

The closest product references are TrainHeroic, Strong, Hevy, and Fitbod. For MVP we should borrow the fast logging loop and avoid advanced recommendations, coaching marketplaces, social feeds, and analytics until local training history is reliable.

## Initial stack

- Kotlin Multiplatform for shared domain, data contracts, presentation, and Compose UI.
- Compose Multiplatform for Android, iOS, and Desktop screens.
- MVI for feature presentation: `Interactor` owns intents, state, effects, reducer application, background bindings, and cancellable jobs.
- Koin for dependency injection across shared modules. Platform hosts start Koin; feature/data/core modules contribute their own DI modules.
- Repository interfaces in domain with SQLDelight-backed local persistence in data/database modules.

## Module shape

GymHelper uses feature-oriented Kotlin Multiplatform modules. Each shared business/UI module owns its own `commonMain` code and platform source sets when it needs them.

- `androidApp`: Android host.
- `iosApp`: iOS host.
- `desktopApp`: Desktop host.
- `shared`: application composition root. It wires modules together and exposes `App()` to platform hosts.
- `core:coroutines`: common coroutine infrastructure.
- `core:mvi`: MVI primitives: `Interactor`, `Reducer`, marker interfaces, managed jobs.
- `core:uikit`: reusable Compose Multiplatform UI components and visual primitives.
- `domain:workout`: workout entities and repository contracts.
- `data:workout`: workout repository implementations.
- `database`: SQLDelight schema, generated database, and platform driver factories.
- `feature:workout-log`: workout logging feature UI, contract, reducer, and interactor.

Dependency direction:

`platform host -> shared -> feature -> domain`

`shared -> data -> domain`

`feature -> core`

`data -> database`

## UI Shape

Feature UI follows a `Route -> Screen -> Content` split:

- `Route` owns feature lifetime, creates/starts/closes the interactor, and collects state.
- `Screen` is the MVI adapter. It receives state plus `onIntent` and maps intents to UI callbacks.
- `Content` is stateless layout. It receives plain callbacks such as `onBack`, `onAddSet`, or `onWeightChange`.

Reusable visual pieces live in `core:uikit`. Feature-specific cards and rows stay inside the feature until another feature genuinely needs them.

## MVI shape

The shared layer uses a CMP-friendly interpretation of an Android interactor MVI pattern:

- `State` is the single source of truth for UI.
- `Intent` represents user actions and internal events, including repository updates mapped back into the feature.
- `Effect` is reserved for one-shot events such as navigation or messages.
- `Reducer<Intent, State>` is pure and synchronous.
- `Interactor` owns coroutine scope, intent queue, effect queue, initial binding, and managed jobs.

The common `Interactor` deliberately does not extend Android `ViewModel`. Android, iOS, and Desktop host it through Compose lifecycle and call `start()` / `close()` from the route.

## DI Shape

Koin modules live near the dependencies they create:

- `core:coroutines` exposes `coroutinesModule`.
- `data:workout` exposes `workoutDataModule`.
- `database` exposes `databaseModule`; platform-specific source sets provide SQLDelight driver factories.
- `feature:workout-log` exposes `workoutLogFeatureModule`.
- `shared` combines them in `appModules` and provides `initKoin()`.

Platform entry points call `initKoin()` before rendering `App()`. Compose screens use injected dependencies from the composition root instead of constructing feature graphs manually.

## Persistence

Workout data is stored locally with SQLDelight:

- Android uses `AndroidSqliteDriver`.
- iOS uses `NativeSqliteDriver`.
- Desktop uses `JdbcSqliteDriver`.

The current repository keeps a `MutableStateFlow` in front of SQLDelight and reloads the active workout after writes. This keeps the first persistence slice simple; repository internals can later switch to SQLDelight reactive query flows without changing feature contracts.

## MVP feature slices

1. Active workout logging: start a workout, add a stage description, record sets with weight and reps, finish the stage.
2. Set editing: edit saved stage sets or delete mistaken draft/saved sets.
3. Workout completion: finish a session and persist it locally.
4. Workout history: browse completed sessions and inspect previous results.
5. Routine templates: create a reusable workout plan.

The first implemented slice is intentionally narrow: active workout logging with temporary in-memory data.
