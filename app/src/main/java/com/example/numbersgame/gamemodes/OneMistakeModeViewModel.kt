package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.utils.getWords

class OneMistakeModeViewModel(application: Application) : GameModeViewModel(application) {
    override val CHAPTER_ID = 2

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()
        setWords(
            true,
            getWords(
                _currentNumber.value ?: "",
                "",
                false
            )
        )
    }

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        super.onMistakeStatusChanged(isMistaken)
        if (isMistaken)
            gameTimer.finish()
    }
}