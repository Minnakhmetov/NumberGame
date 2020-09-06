package com.blueberrybeegames.numbersgame.gamemodes

import android.app.Application
import com.blueberrybeegames.numbersgame.GameApplication
import com.blueberrybeegames.numbersgame.utils.NumberReader
import com.blueberrybeegames.numbersgame.R
import com.blueberrybeegames.numbersgame.utils.DelayedSpannableStringBuilder

open class VoiceModeViewModel(application: Application) : GameModeViewModel(application) {

    companion object {
        const val CHAPTER_ID = "voice_mode"
    }

    override val chapterId: String = CHAPTER_ID

    protected val numberReader = NumberReader(application, soundPool)

    private var isFirstRound = true

    override fun getTimeForNumberInSec(length: Int) = when (length) {
        1 -> 4
        2 -> 5
        3 -> 6
        4 -> 8
        5 -> 8
        6 -> 9
        7 -> 10
        8 -> 10
        9 -> 11
        else -> 0
    }.toLong()

    fun loadNewNumberSound() {
        numberReader.load(currentNumber.value ?: "")
    }

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()
        numberReader.onLoadCompleteListener = {
            numberReader.start(!isFirstRound)
        }
        loadNewNumberSound()
        isFirstRound = false
        setWords(
            true,
            DelayedSpannableStringBuilder(
                getApplication<GameApplication>().getString(R.string.tap_to_listen)
            )
        )
    }

    override fun onMistakeStatusChanged(isMistaken: Boolean) {
        super.onMistakeStatusChanged(isMistaken)
        _mistakeFrame.value = isMistaken
    }

    override fun onGameStarted() {
        super.onGameStarted()
        setWords(
            true,
            DelayedSpannableStringBuilder(getApplication<GameApplication>().getString(R.string.tap_to_listen))
        )
    }

    override fun onWordsClick() {
        super.onWordsClick()
        numberReader.start()
    }

    override fun onGameFinished() {
        super.onGameFinished()
        numberReader.stop()
    }

    override fun onGamePaused() {
        super.onGamePaused()
        numberReader.stop()
    }
}