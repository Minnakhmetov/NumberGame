package com.example.numbersgame.gamemodes

import android.app.Application
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.text.SpannableStringBuilder
import androidx.lifecycle.*
import com.example.numbersgame.*
import com.example.numbersgame.R
import com.example.numbersgame.storage.RecordsStorage
import com.example.numbersgame.utils.getRandomNumber
import timber.log.Timber
import kotlin.math.min
import kotlin.properties.Delegates

abstract class GameModeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val NOT_STARTED = 0
        const val STARTED = 1
        const val FINISHED = 2

        const val BLUNDER = 0
        const val TIME_IS_UP = 1

        const val BACKSPACE = 10

        private const val COUNTDOWN: Long = 3000
        private const val INITIAL_GAME_LENGTH: Long = 7000
    }

    abstract val CHAPTER_ID: Int

    private var countDownTimer: CountDownTimer = object : CountDownTimer(COUNTDOWN, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            secondsBeforeStart.value = millisUntilFinished / 1000
        }

        override fun onFinish() {
            startGame()
        }
    }

    private var minNumberLength: Int = 1
    private var maxNumberLength: Int = 1


    private val secondsBeforeStart = MutableLiveData<Long>()
    val countdownString: LiveData<String> =
        Transformations.map(secondsBeforeStart) { it.toString() }

    private val _userInput = MutableLiveData<String>("")
    val userInput: LiveData<String>
        get() = _userInput

    protected val currentNumber = MutableLiveData<String>()

    private val _words = MutableLiveData<Pair<Boolean, SpannableStringBuilder>>()
    val words: LiveData<Pair<Boolean, SpannableStringBuilder>> = _words

    private val _score = MutableLiveData(0)
    val formattedScore: LiveData<String> = Transformations.map(_score) { score ->
        application.getString(R.string.score, score)
    }

    private val _gameState = MutableLiveData<Int>(NOT_STARTED)
    val gameState: LiveData<Int> = _gameState

    private val _secondsLeft = MutableLiveData<String>()
    val secondsLeft: LiveData<String> = _secondsLeft

    protected lateinit var gameTimer: CountDownTimer

    private val _answer = MutableLiveData<String>("")
    val answer: LiveData<String> = _answer

    protected val _mistakeFrame = MutableLiveData<Boolean>()
    val mistake: LiveData<Boolean>
        get() = _mistakeFrame

    private val _gameEndMessage = MutableLiveData<String>()
    val gameEndMessage: LiveData<String> = _gameEndMessage

    private var successId = -1
    private var failureId = -1

    val finalScore
        get() = _score.value ?: 0

    private val soundPool = if (Build.VERSION.SDK_INT >= 21) {
        SoundPool.Builder()
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
            .setMaxStreams(4)
            .build()
    }
    else {
        SoundPool(4, AudioManager.STREAM_MUSIC, 100)
    }

    fun startCountdown() {
        if (gameState.value != NOT_STARTED)
            return
        countDownTimer.start()
    }

    abstract fun getExtraTime(length: Int): Int

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
        startNewRound()
        onGameStarted()

        failureId = soundPool.load(getApplication(), R.raw.failure, 0)
        successId = soundPool.load(getApplication(), R.raw.success, 0)

        gameTimer = object : CountDownTimer(INITIAL_GAME_LENGTH, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _secondsLeft.value = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                _gameState.value = FINISHED
                finishGame(TIME_IS_UP)
            }
        }

        startGameTimer()
    }

    protected fun finishGame(msg: Int) {
        RecordsStorage(getApplication())
            .saveRecord(CHAPTER_ID, _score.value ?: 0)
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

    private fun startNewRound() {
        currentNumber.value = getRandomNumber(minNumberLength, maxNumberLength)
        onCurrentNumberChanged()
        _userInput.value = ""
        onUserInputChanged()
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
            currentNumber.value?.let {
                gameTimer.addTime(getExtraTime(it.length))
            }
            _score.value = (_score.value ?: 0) + 1
            soundPool.play(successId, 1F, 1F, 0, 0, 1F)
            minNumberLength = min(9, (_score.value ?: 0) + 1)
            maxNumberLength = minNumberLength
            startNewRound()
        }
    }

    protected open fun onCurrentNumberChanged() {

    }

    protected open fun onMistakeStatusChanged(isMistaken: Boolean) {
        _mistakeFrame.value = isMistaken
    }

    protected fun setWords(animate: Boolean, newWords: SpannableStringBuilder) {
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