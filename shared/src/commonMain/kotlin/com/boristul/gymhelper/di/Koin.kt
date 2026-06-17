package com.boristul.gymhelper.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun initKoin(
    extraModules: List<Module> = platformKoinModules(),
    appDeclaration: KoinApplication.() -> Unit = {},
) {
    if (GlobalContext.getOrNull() != null) return

    startKoin {
        appDeclaration()
        modules(extraModules + appModules)
    }
}

expect fun platformKoinModules(): List<Module>
