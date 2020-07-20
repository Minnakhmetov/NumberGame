package com.example.numbersgame

import android.app.Application
import android.content.Context

class RecordsStorage(private val context: Context) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.records_file_key), Context.MODE_PRIVATE)
    }

    fun saveRecord(chapterId: Int, record: Int) {
        sharedPreferences.edit()
            .putInt(context.getString(R.string.chapter_key, chapterId), record)
            .apply()
    }

    fun getRecord(chapterId: Int): Int {
        return sharedPreferences.getInt(context.getString(R.string.chapter_key, chapterId), 0)
    }
}