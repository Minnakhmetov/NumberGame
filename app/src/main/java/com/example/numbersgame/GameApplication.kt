package com.example.numbersgame

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

class GameApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

}