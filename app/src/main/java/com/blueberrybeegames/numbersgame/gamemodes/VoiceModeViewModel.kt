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

    private val numberReader = NumberReader(application, soundPool)

    private var isFirstRound = true

    override fun getTimeForNumberInSec(length: Int) = when (length) {
        1 -> 3
        2 -> 4
        3 -> 5
        4 -> 6
        5 -> 6
        6 -> 7
        7 -> 8
        8 -> 8
        9 -> 9
        else -> 0
    }.toLong()

    fun loadNewNumberSound() {
        numberReader.load(currentNumber.value ?: "")
    }

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()
        loadNewNumberSound()
        numberReader.onLoadCompleteListener = {
            numberReader.start(!isFirstRound)
        }
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