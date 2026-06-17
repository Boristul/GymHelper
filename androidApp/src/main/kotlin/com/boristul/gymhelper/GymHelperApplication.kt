package com.boristul.gymhelper

import android.app.Application
import com.boristul.gymhelper.di.initKoin

class GymHelperApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}
