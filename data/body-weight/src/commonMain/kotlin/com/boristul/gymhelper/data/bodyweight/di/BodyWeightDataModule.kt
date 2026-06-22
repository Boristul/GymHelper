package com.boristul.gymhelper.data.bodyweight.di

import com.boristul.gymhelper.data.bodyweight.SqlBodyWeightRepository
import com.boristul.gymhelper.domain.bodyweight.BodyWeightRepository
import org.koin.dsl.module

val bodyWeightDataModule = module {
    single<BodyWeightRepository> {
        SqlBodyWeightRepository(
            database = get(),
        )
    }
}
