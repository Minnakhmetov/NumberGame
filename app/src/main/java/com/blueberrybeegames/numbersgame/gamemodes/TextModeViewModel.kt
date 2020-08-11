package com.blueberrybeegames.numbersgame.gamemodes

import android.app.Application
import com.blueberrybeegames.numbersgame.utils.getWords

open class TextModeViewModel(application: Application) : GameModeViewModel(application) {

    companion object {
        const val CHAPTER_ID = "text_mode"
    }

    override val chapterId: String = CHAPTER_ID

    override fun getTimeForNumberInSec(length: Int) = when (length) {
        1 -> 2
        2 -> 3
        3 -> 4
        4 -> 5
        5 -> 5
        6 -> 6
        7 -> 7
        8 -> 7
        9 -> 8
        else -> 0
    }.toLong()

    override fun onUserInputChanged() {
        super.onUserInputChanged()

        setWords(false, getWords(
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