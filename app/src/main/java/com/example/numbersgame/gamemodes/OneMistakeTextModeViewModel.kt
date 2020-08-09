package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.R
import com.example.numbersgame.utils.getWords

class OneMistakeTextModeViewModel(application: Application) : TextModeViewModel(application) {

    companion object {
        const val CHAPTER_ID = "one_mistake_text_mode"
    }

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