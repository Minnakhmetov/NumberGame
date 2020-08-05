package com.example.numbersgame.models

import android.content.Context
import com.example.numbersgame.R
import com.example.numbersgame.storage.RecordsStorage

class ChapterRepository(private val context: Context) {
    fun getChapters(): List<Chapter> {
        val recordsStorage = RecordsStorage(context)

        return context.run {
            listOf(
                Chapter(
                    getString(R.string.text_mode_id),
                    getString(R.string.text_mode_name),
                    getString(R.string.spelling_category),
                    recordsStorage
                ),
                Chapter(
                    getString(R.string.one_mistake_text_mode_id),
                    getString(R.string.one_mistake_text_mode_name),
                    getString(R.string.spelling_category),
                    recordsStorage
                ),
                Chapter(
                    getString(R.string.voice_mode_id),
                    getString(R.string.voice_mode_name),
                    getString(R.string.listening_category),
                    recordsStorage
                ),
                Chapter(
                    getString(R.string.one_mistake_voice_mode_id),
                    getString(R.string.one_mistake_voice_mode_name),
                    getString(R.string.listening_category),
                    recordsStorage
                )
            )
        }
    }

    fun getChaptersGroupedByCategory(): Map<String, List<Chapter>> {
        return getChapters().groupBy { it.category }
    }
}