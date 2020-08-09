package com.example.numbersgame.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.numbersgame.MainActivity
import com.example.numbersgame.R
import timber.log.Timber
import java.lang.ref.WeakReference

private const val SETTINGS_FILE_KEY = "setting_file_key"
private const val THEME_ID_KEY = "themeId"

class ThemeKeeper private constructor() {

    private lateinit var settingsSharedPreferences: SharedPreferences
    private var subscriber: WeakReference<MainActivity>? = null
    var themeId: Int = -1
        set(value) {
            if (field == value)
                return
            settingsSharedPreferences.edit().putInt(THEME_ID_KEY, value).apply()
            field = value
            subscriber?.get()?.recreate()
        }
        get() {
            if (field == -1) {
                field = settingsSharedPreferences.getInt(THEME_ID_KEY, R.style.Green)
            }
            return field
        }

    companion object {
        private var INSTANCE: ThemeKeeper? = null

        fun getInstance(context: Context): ThemeKeeper {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = ThemeKeeper().apply {
                        settingsSharedPreferences = context.getSharedPreferences(
                            "${context.packageName}.$SETTINGS_FILE_KEY", Context.MODE_PRIVATE
                        )
                    }
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    fun subscribe(activity: MainActivity) {
        subscriber = WeakReference(activity)
    }
}