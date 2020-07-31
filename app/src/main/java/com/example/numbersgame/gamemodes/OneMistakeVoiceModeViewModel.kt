package com.example.numbersgame.gamemodes

import android.app.Application
import android.text.SpannableStringBuilder
import com.example.numbersgame.GameApplication
import com.example.numbersgame.utils.NumberReader
import com.example.numbersgame.R

class OneMistakeVoiceModeViewModel(application: Application) : VoiceModeViewModel(application) {
    override val CHAPTER_ID = 4

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        if (isMistaken)
            gameTimer.cancel()
    }
}