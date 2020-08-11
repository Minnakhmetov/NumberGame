package com.blueberrybeegames.numbersgame.gamemodes

import android.app.Application

class OneMistakeVoiceModeViewModel(application: Application) : VoiceModeViewModel(application) {

    companion object {
        const val CHAPTER_ID = "one_mistake_voice_mode"
    }

    override val chapterId: String = CHAPTER_ID

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        if (isMistaken) {
            gameTimer.pause()
            finishGame(BLUNDER)
        }
    }
}