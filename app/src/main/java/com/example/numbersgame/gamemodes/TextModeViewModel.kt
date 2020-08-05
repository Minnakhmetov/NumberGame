package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.R
import com.example.numbersgame.utils.getWords

open class TextModeViewModel(application: Application) : GameModeViewModel(application) {
    override val CHAPTER_ID = application.getString(R.string.text_mode_id)

    override fun getExtraTime(length: Int) = length / 2

    override fun onUserInputChanged() {
        super.onUserInputChanged()

        setWords(false, getWords(
            getApplication(),
            currentNumber.value ?: "",
            userInput.value ?: "",
            true
        )
        )
    }

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()

        setWords(true,
            getWords(
                getApplication(),
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