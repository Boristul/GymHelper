package com.boristul.gymhelper.core.coroutines.di

import com.boristul.gymhelper.core.coroutines.AppDispatchers
import org.koin.dsl.module

val coroutinesModule = module {
    single { AppDispatchers() }
}
