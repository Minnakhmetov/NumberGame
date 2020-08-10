package com.example.numbersgame.gamemodes

import android.app.Application
import android.text.SpannableStringBuilder
import com.example.numbersgame.GameApplication
import com.example.numbersgame.utils.NumberReader
import com.example.numbersgame.R
import com.example.numbersgame.utils.DelayedSpannableStringBuilder
import timber.log.Timber

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

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()
        numberReader.load(currentNumber.value ?: "")
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