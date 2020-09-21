package com.blueberrybeegames.numbersgame.storage

import android.content.Context
import com.blueberrybeegames.numbersgame.gamemodes.VoiceModeViewModel
import kotlin.math.max

private const val RECORDS_FILE_KEY = "records_file_key"

class RecordsStorage(private val context: Context) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences("${context.packageName}.${RECORDS_FILE_KEY}", Context.MODE_PRIVATE)
    }

    fun saveRecord(chapterId: String, record: Int) {
        val currentRecord = sharedPreferences.getInt(chapterId, 0)
        sharedPreferences.edit()
            .putInt(chapterId, max(currentRecord, record))
            .apply()
    }

    fun getRecord(chapterId: String): Int {
        return sharedPreferences.getInt(chapterId, 0)
    }
}