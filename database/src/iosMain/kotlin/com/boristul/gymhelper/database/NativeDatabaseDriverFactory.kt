package com.boristul.gymhelper.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class NativeDatabaseDriverFactory(
    private val config: DatabaseConfig = DatabaseConfig(),
) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = GymHelperDatabase.Schema,
            name = config.name,
        )
    }
}
