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
                _currentNumber.value ?: "",
                "",
                false
            )
        )
    }

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        if (isMistaken)
            gameTimer.finish()
    }
}