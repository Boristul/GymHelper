package com.boristul.gymhelper.database.di

import app.cash.sqldelight.db.SqlDriver
import com.boristul.gymhelper.database.DatabaseDriverFactory
import com.boristul.gymhelper.database.GymHelperDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<SqlDriver> { get<DatabaseDriverFactory>().createDriver() }
    single { GymHelperDatabase(get()) }
}
