package com.boristul.gymhelper.di

import android.content.Context
import com.boristul.gymhelper.database.AndroidDatabaseDriverFactory
import com.boristul.gymhelper.database.DatabaseDriverFactory
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformKoinModules(): List<Module> = emptyList()

fun initKoin(
    context: Context,
    appDeclaration: KoinApplication.() -> Unit = {},
) {
    initKoin(
        extraModules = listOf(
            module {
                single<DatabaseDriverFactory> {
                    AndroidDatabaseDriverFactory(context.applicationContext)
                }
            }
        ),
        appDeclaration = appDeclaration,
    )
}
