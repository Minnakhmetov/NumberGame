package com.blueberrybeegames.numbersgame.gamemodes

import android.app.Application
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
    }

    override fun onWordsClick() {
        if (numberReader.loadedNumber != currentNumber.value)
            loadNewNumberSound()
        else
            numberReader.start()
    }

    override fun onUserInputChanged() {
        currentNumber.value = userInput.value

        setWords(false,
            getWords(
                userInput.value ?: "",
                "",
                false
            )
        )
    }
}