package com.boristul.gymhelper.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(
    private val context: Context,
    private val config: DatabaseConfig = DatabaseConfig(),
) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = GymHelperDatabase.Schema,
            context = context,
            name = config.name,
        )
    }
}
