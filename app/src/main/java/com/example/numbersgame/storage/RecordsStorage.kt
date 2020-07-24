package com.example.numbersgame.storage

import android.content.Context
import com.example.numbersgame.R
import kotlin.math.max

class RecordsStorage(private val context: Context) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.records_file_key), Context.MODE_PRIVATE)
    }

    fun saveRecord(chapterId: Int, record: Int) {
        val currentRecord = sharedPreferences.getInt(context.getString(R.string.chapter_key, chapterId), 0)
        sharedPreferences.edit()
            .putInt(context.getString(R.string.chapter_key, chapterId), max(currentRecord, record))
            .apply()
    }

    fun getRecord(chapterId: Int): Int {
        return sharedPreferences.getInt(context.getString(R.string.chapter_key, chapterId), 0)
    }
}