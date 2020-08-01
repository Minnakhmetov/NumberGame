package com.example.numbersgame.gamemodes

import android.app.Application

class OneMistakeVoiceModeViewModel(application: Application) : VoiceModeViewModel(application) {
    override val CHAPTER_ID = 4

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        if (isMistaken) {
            gameTimer.pause()
            finishGame(BLUNDER)
        }
    }
}