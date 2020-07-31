package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.utils.getWords

open class TextModeViewModel(application: Application) : GameModeViewModel(application) {
    override val CHAPTER_ID = 1

    override fun onUserInputChanged() {
        super.onUserInputChanged()

        setWords(false, getWords(
            currentNumber.value ?: "",
            _userInput.value ?: "",
            true
        )
        )
    }

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()

        setWords(true,
            getWords(
                currentNumber.value ?: "",
                userInput.value ?: "",
                true
            )
        )
    }

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        super.onMistakeStatusChanged(isMistaken)
        _mistakeFrame.value = isMistaken
    }
}