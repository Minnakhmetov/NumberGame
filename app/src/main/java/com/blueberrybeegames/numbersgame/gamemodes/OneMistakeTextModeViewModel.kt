package com.blueberrybeegames.numbersgame.gamemodes

import android.app.Application
import com.blueberrybeegames.numbersgame.utils.getWords

class OneMistakeTextModeViewModel(application: Application) : TextModeViewModel(application) {

    companion object {
        const val CHAPTER_ID = "one_mistake_text_mode"
    }

    override fun getTimeForNumberInSec(length: Int) = when (length) {
        1 -> 2
        2 -> 2
        3 -> 3
        4 -> 4
        5 -> 4
        6 -> 5
        7 -> 6
        8 -> 6
        9 -> 7
        else -> 0
    }.toLong()

    override val chapterId: String = CHAPTER_ID

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