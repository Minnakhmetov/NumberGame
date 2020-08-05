package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.R

class OneMistakeVoiceModeViewModel(application: Application) : VoiceModeViewModel(application) {
    override val CHAPTER_ID = application.getString(R.string.one_mistake_voice_mode_id)

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        if (isMistaken) {
            gameTimer.pause()
            finishGame(BLUNDER)
        }
    }
}