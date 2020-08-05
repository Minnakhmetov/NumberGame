package com.example.numbersgame.storage

import android.content.Context
import com.example.numbersgame.R
import kotlin.math.max

class RecordsStorage(private val context: Context) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.records_file_key), Context.MODE_PRIVATE)
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