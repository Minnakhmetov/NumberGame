package com.blueberrybeegames.numbersgame.models

import android.content.Context
import com.blueberrybeegames.numbersgame.R
import com.blueberrybeegames.numbersgame.gamemodes.*
import com.blueberrybeegames.numbersgame.storage.RecordsStorage

class ChapterRepository(private val context: Context) {
    fun getChapters(): List<Chapter> {
        val recordsStorage = RecordsStorage(context)

        return context.run {
            listOf(
                Chapter(
                    TextModeViewModel.CHAPTER_ID,
                    getString(R.string.text_mode_name),
                    getString(R.string.spelling_category),
                    recordsStorage
                ),
                Chapter(
                    OneMistakeTextModeViewModel.CHAPTER_ID,
                    getString(R.string.one_mistake_text_mode_name),
                    getString(R.string.spelling_category),
                    recordsStorage
                ),
                Chapter(
                    VoiceModeViewModel.CHAPTER_ID,
                    getString(R.string.voice_mode_name),
                    getString(R.string.listening_category),
                    recordsStorage
                ),
                Chapter(
                    OneMistakeVoiceModeViewModel.CHAPTER_ID,
                    getString(R.string.one_mistake_voice_mode_name),
                    getString(R.string.listening_category),
                    recordsStorage
                ),
                Chapter(
                    SandboxModeViewModel.CHAPTED_ID,
                    "Sandbox",
                    0,
                    "Research"
                )
            )
        }
    }

    fun getChaptersGroupedByCategory(): Map<String, List<Chapter>> {
        return getChapters().groupBy { it.category }
    }
}