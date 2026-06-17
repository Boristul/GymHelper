package com.boristul.gymhelper.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

class JvmDatabaseDriverFactory(
    private val config: DatabaseConfig = DatabaseConfig(),
) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        val databaseFile = File(config.name)
        val shouldCreateSchema = !databaseFile.exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.path}")

        if (shouldCreateSchema) {
            GymHelperDatabase.Schema.create(driver)
        }

        return driver
    }
}
