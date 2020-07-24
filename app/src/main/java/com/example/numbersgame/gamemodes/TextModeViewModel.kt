package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.getWords

class TextModeViewModel(application: Application) : GameModeViewModel(application) {
    override val CHAPTER_ID = 1

    override fun onUserInputChanged() {
        super.onUserInputChanged()

        setWords(false, getWords(
            _currentNumber.value ?: "",
            _userInput.value ?: "",
            true
        ))
    }

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()

        setWords(true,
            getWords(
                _currentNumber.value ?: "",
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