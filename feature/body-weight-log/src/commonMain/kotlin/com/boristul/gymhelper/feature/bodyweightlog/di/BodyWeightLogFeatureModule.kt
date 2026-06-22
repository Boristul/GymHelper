package com.boristul.gymhelper.feature.bodyweightlog.di

import com.boristul.gymhelper.feature.bodyweightlog.BodyWeightLogInteractor
import org.koin.dsl.module

val bodyWeightLogFeatureModule = module {
    factory {
        BodyWeightLogInteractor(
            bodyWeightRepository = get(),
            dispatchers = get(),
        )
    }
}
