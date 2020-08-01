package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.utils.getWords

class OneMistakeTextModeViewModel(application: Application) : TextModeViewModel(application) {
    override val CHAPTER_ID = 3

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()
        setWords(
            true,
            getWords(
                currentNumber.value ?: "",
                "",
                false
            )
        )
    }

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        if (isMistaken) {
            gameTimer.pause()
            finishGame(BLUNDER)
        }
    }
}