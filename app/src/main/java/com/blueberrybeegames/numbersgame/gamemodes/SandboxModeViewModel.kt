package com.blueberrybeegames.numbersgame.gamemodes

import android.app.Application
import com.blueberrybeegames.numbersgame.GameApplication
import com.blueberrybeegames.numbersgame.R
import com.blueberrybeegames.numbersgame.utils.DelayedSpannableStringBuilder
import com.blueberrybeegames.numbersgame.utils.getWords

class SandboxModeViewModel(application: Application) : VoiceModeViewModel(application) {

    companion object {
        const val CHAPTED_ID = "sandbox_mode"
    }

    override fun onGameStarted() {}
    override fun onMistakeStatusChanged(isMistaken: Boolean) {}
    override fun onGameResumed() {}

    override fun initialize() {
        _gameState.value = STARTED
        numberReader.onLoadCompleteListener = {
            numberReader.start(false)
        }
        setWords(true,
            DelayedSpannableStringBuilder(
                getApplication<GameApplication>().getString(R.string.tap_on_number_to_listen)
            )
        )
    }

    override fun onWordsClick() {
        if (numberReader.loadedNumber != currentNumber.value)
            loadNewNumberSound()
        else
            numberReader.start()
    }

    override fun onUserInputChanged() {
        currentNumber.value = userInput.value

        if (userInput.value.isNullOrEmpty()) {
            setWords(true,
                DelayedSpannableStringBuilder(
                    getApplication<GameApplication>().getString(R.string.tap_on_number_to_listen)
                )
            )
        }
        else {
            setWords(true,
                getWords(
                    userInput.value ?: "",
                    "",
                    false
                )
            )
        }


    }
}