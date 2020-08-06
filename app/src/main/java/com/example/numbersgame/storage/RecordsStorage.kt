package com.example.numbersgame.storage

import android.content.Context
import com.example.numbersgame.R
import kotlin.math.max

private const val RECORDS_FILE_KEY = "com.example.numbersgame.records_file_key"

class RecordsStorage(private val context: Context) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(RECORDS_FILE_KEY, Context.MODE_PRIVATE)
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