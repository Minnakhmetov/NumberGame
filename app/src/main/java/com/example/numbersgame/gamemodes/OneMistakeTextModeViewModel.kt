package com.example.numbersgame.gamemodes

import android.app.Application
import com.example.numbersgame.R
import com.example.numbersgame.utils.getWords

class OneMistakeTextModeViewModel(application: Application) : TextModeViewModel(application) {
    override val CHAPTER_ID = application.getString(R.string.one_mistake_text_mode_id)

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()
        setWords(
            true,
            getWords(
                getApplication(),
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