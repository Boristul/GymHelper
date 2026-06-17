package com.boristul.gymhelper.di

import com.boristul.gymhelper.database.DatabaseDriverFactory
import com.boristul.gymhelper.database.NativeDatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformKoinModules(): List<Module> {
    return listOf(
        module {
            single<DatabaseDriverFactory> { NativeDatabaseDriverFactory() }
        }
    )
}
