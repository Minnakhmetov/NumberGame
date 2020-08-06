package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.R
import com.example.numbersgame.utils.getWords

open class TextModeViewModel(application: Application) : GameModeViewModel(application) {

    companion object {
        const val CHAPTER_ID = "text_mode"
    }

    override val chapterId: String = CHAPTER_ID

    override fun getTimeForNumberInSec(length: Int) = (length.toLong() + 2) / 2 + 1

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