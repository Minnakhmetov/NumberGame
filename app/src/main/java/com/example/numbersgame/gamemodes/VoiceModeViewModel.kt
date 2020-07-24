package com.example.numbersgame.gamemodes

import android.app.Application
import android.text.SpannableStringBuilder
import com.example.numbersgame.GameApplication
import com.example.numbersgame.utils.NumberReader
import com.example.numbersgame.R

class VoiceModeViewModel(application: Application) : GameModeViewModel(application) {
    override val CHAPTER_ID: Int = 3

    override val minNumberLength: Int = 1
    override val maxNumberLength: Int = 6

    private val numberReader = NumberReader()

    override fun onCurrentNumberChanged() {
        super.onCurrentNumberChanged()
        numberReader.load(getApplication(), _currentNumber.value ?: "")
        numberReader.start()
        setWords(
            true,
            SpannableStringBuilder(getApplication<GameApplication>().getString(R.string.tap_to_listen))
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
            SpannableStringBuilder(getApplication<GameApplication>().getString(R.string.tap_to_listen)))
    }

    override fun onWordsClick() {
        super.onWordsClick()
        numberReader.start()
    }
}