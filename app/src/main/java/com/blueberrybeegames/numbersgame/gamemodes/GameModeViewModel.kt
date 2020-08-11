package com.blueberrybeegames.numbersgame.gamemodes

import android.app.Application
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.*
import com.blueberrybeegames.numbersgame.*
import com.blueberrybeegames.numbersgame.R
import com.blueberrybeegames.numbersgame.storage.RecordsStorage
import com.blueberrybeegames.numbersgame.utils.DelayedSpannableStringBuilder
import com.blueberrybeegames.numbersgame.utils.getRandomNumber
import kotlin.math.min

abstract class GameModeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val NOT_STARTED = 0
        const val STARTED = 1
        const val FINISHED = 2

        const val BLUNDER = 0
        const val TIME_IS_UP = 1

        const val BACKSPACE = 10

        private const val COUNTDOWN: Long = 3000
    }

    abstract val chapterId: String

    private var countDownTimer: CountDownTimer = object : CountDownTimer(1000) {
        override fun onTick(millisUntilFinished: Long) {
            secondsBeforeStart.value = millisUntilFinished / 1000
        }

        override fun onFinish() {
            startGame()
        }
    }.apply {
        length = COUNTDOWN
    }

    private var minNumberLength: Int = 1
    private var maxNumberLength: Int = 9


    private val secondsBeforeStart = MutableLiveData<Long>()
    val countdownString: LiveData<String> =
        Transformations.map(secondsBeforeStart) { it.toString() }

    private val _userInput = MutableLiveData<String>("")
    val userInput: LiveData<String> = _userInput

    protected val currentNumber = MutableLiveData<String>()

    private val _words = MutableLiveData<Pair<Boolean, DelayedSpannableStringBuilder>>()
    val words: LiveData<Pair<Boolean, DelayedSpannableStringBuilder>> = _words

    private val _score = MutableLiveData(0)
    val formattedScore: LiveData<String> = Transformations.map(_score) { score ->
        score.toString()
    }

    protected val _gameState = MutableLiveData<Int>(NOT_STARTED)
    val gameState: LiveData<Int> = _gameState

    private val _secondsLeft = MutableLiveData<String>()
    val secondsLeft: LiveData<String> = _secondsLeft

    protected lateinit var gameTimer: CountDownTimer

    private val _answer = MutableLiveData<String>("")
    val answer: LiveData<String> = _answer

    protected val _mistakeFrame = MutableLiveData<Boolean>()
    val mistake: LiveData<Boolean> = _mistakeFrame

    private val _gameEndMessage = MutableLiveData<String>()
    val gameEndMessage: LiveData<String> = _gameEndMessage

    private var successId = -1
    private var failureId = -1

    val finalScore
        get() = _score.value ?: 0

    protected val soundPool = SoundPool.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build()
        )
        .setMaxStreams(4)
        .build()

    open fun initialize() {
        startCountdown()
    }

    private fun startCountdown() {
        if (gameState.value != NOT_STARTED)
            return
        countDownTimer.start()
    }

    open fun getTimeForNumberInSec(length: Int): Long = length + 1L

    protected open fun onGameFinished() {

    }

    private fun resumeCountdown() {
        startCountdown()
    }

    private fun stopCountdown() {
        countDownTimer.pause()
    }

    fun startGame() {
        _gameState.value = STARTED
        onGameStarted()

        failureId = soundPool.load(getApplication(), R.raw.failure, 0)
        successId = soundPool.load(getApplication(), R.raw.success, 0)

        gameTimer = object : CountDownTimer(1000) {
            override fun onTick(millisUntilFinished: Long) {
                _secondsLeft.value = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                _gameState.value = FINISHED
                finishGame(TIME_IS_UP)
            }
        }

        startNewRound()
    }

    protected fun finishGame(msg: Int) {
        soundPool.play(failureId, 1F, 1F, 0, 0, 1F)
        _answer.value =
            getApplication<GameApplication>().getString(R.string.answer, currentNumber.value)
        _gameState.value = FINISHED
        _gameEndMessage.value = getApplication<GameApplication>().getString(
            when (msg) {
                BLUNDER -> R.string.blunder
                else -> R.string.time_is_up
            }
        )
        onGameFinished()
    }

    private fun getLengthRange(currentScore: Int): Pair<Int, Int> {
        val shift = currentScore / 3
        return Pair(min(9, 1 + shift), min(9, 2 + shift))
    }

    private fun startNewRound() {
        if (gameState.value != FINISHED) {
            val (minLength, maxLength) = getLengthRange(_score.value ?: 0)
            val newNumber = getRandomNumber(minLength, maxLength)

            currentNumber.value = newNumber
            gameTimer.length = getTimeForNumberInSec(newNumber.length) * 1000
            gameTimer.start()
            onCurrentNumberChanged()
            _userInput.value = ""
            onUserInputChanged()
        }
    }

    private fun startGameTimer() {
        if (gameState.value == STARTED) {
            gameTimer.start()
        }
    }

    private fun stopGameTimer() {
        if (this::gameTimer.isInitialized)
            gameTimer.pause()
    }

    open fun onGamePaused() {
        stopCountdown()
        stopGameTimer()
    }

    open fun onGameResumed() {
        if (gameState.value == FINISHED)
            return
        startCountdown()
        startGameTimer()
    }

    protected open fun onGameStarted() {

    }

    open fun onWordsClick() {

    }

    protected open fun onUserInputChanged() {
        if (_userInput.value == currentNumber.value) {
            gameTimer.pause()

            _score.value?.let {
                _score.value = it + 1
                RecordsStorage(getApplication())
                    .saveRecord(chapterId, it + 1)
            }

            soundPool.play(successId, 1F, 1F, 0, 0, 1F)
            startNewRound()
        }
    }

    protected open fun onCurrentNumberChanged() {

    }

    protected open fun onMistakeStatusChanged(isMistaken: Boolean) {
        _mistakeFrame.value = isMistaken
    }

    protected fun setWords(animate: Boolean, newWords: DelayedSpannableStringBuilder) {
        _words.value = Pair(animate, newWords)
    }

    open fun handleButtonClick(id: Int) {
        when (id) {
            BACKSPACE -> {
                _userInput.value?.let {
                    if (it.isNotEmpty()) {
                        _userInput.value = it.substring(0, it.length - 1)
                    }
                }
            }
            else -> {
                _userInput.value = (_userInput.value ?: "") + id.toString()
            }
        }

        onUserInputChanged()

        _userInput.value?.let { prefix ->
            currentNumber.value?.let { number ->
                onMistakeStatusChanged(!number.startsWith(prefix))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopGameTimer()
        soundPool.release()
    }
}