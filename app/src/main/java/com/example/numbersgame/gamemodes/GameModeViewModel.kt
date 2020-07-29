package com.example.numbersgame.gamemodes

import android.app.Application
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import androidx.lifecycle.*
import com.example.numbersgame.*
import com.example.numbersgame.R
import com.example.numbersgame.storage.RecordsStorage
import com.example.numbersgame.utils.getRandomNumber
import timber.log.Timber
import kotlin.math.min

abstract class GameModeViewModel(application: Application) : AndroidViewModel(application) {
    private val COUNTDOWN: Long = 3000
    private val GAME_LENGTH: Long = 30000
    abstract val CHAPTER_ID: Int

    private var countDownTimer: CountDownTimer? = null
    protected var gameTimer = GameTimer()

    private val _gameStarted = MutableLiveData<Boolean>(false)
    val gameStarted: LiveData<Boolean>
        get() = _gameStarted

    private var minNumberLength: Int = 1
    private var maxNumberLength: Int = 1

    val BACKSPACE = 10

    private val secondsBeforeStart = MutableLiveData<Long>()
    val countdownString: LiveData<String> =
        Transformations.map(secondsBeforeStart) { (it + 1).toString() }

    protected val _userInput = MutableLiveData<String>("")
    val userInput: LiveData<String>
        get() = _userInput

    protected val _currentNumber = MutableLiveData<String>()

    val words = MediatorLiveData<Pair<Boolean, SpannableStringBuilder>>()

    val timerString: LiveData<String> =
        Transformations.map(gameTimer.secondsLeft) { (it + 1).toString() }

    protected val _score = MutableLiveData(0)
    val formattedScore = Transformations.map(_score) { score ->
        application.getString(R.string.score, score)
    }

    val gameFinishEvent = Transformations.map(gameTimer.finished) { event ->
        if (event.peekContent())
            onGameFinish()
        event
    }

    protected val _mistakeFrame = MutableLiveData<Boolean>()
    val mistake: LiveData<Boolean>
        get() = _mistakeFrame

    val finalScore
        get() = _score.value ?: 0

    fun startCountdown() {
        stopCountdown()

        countDownTimer = object : CountDownTimer(
            secondsBeforeStart.value?.times(1000) ?: COUNTDOWN,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                secondsBeforeStart.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                countDownTimer = null
                startGame()
            }
        }

        countDownTimer?.start()
    }

    open fun onGameFinish() {
        RecordsStorage(getApplication())
            .saveRecord(CHAPTER_ID, _score.value ?: 0)
        MediaPlayer.create(getApplication(), R.raw.failure).start()
    }

    fun resumeCountdown() {
        if (countDownTimer != null)
            startCountdown()
    }

    fun resumeGameTimer() {
        gameTimer.resume()
    }

    fun stopCountdown() {
        countDownTimer?.cancel()
    }

    fun startGame() {
        startGameTimer()
        startNewRound()
        onGameStarted()
        _gameStarted.value = true
    }

    private fun startNewRound() {
        _currentNumber.value = getRandomNumber(minNumberLength, maxNumberLength)
        onCurrentNumberChanged()
        _userInput.value = ""
        onUserInputChanged()
    }

    fun startGameTimer() {
        gameTimer.start(GAME_LENGTH)
    }

    fun stopGameTimer() {
        gameTimer.pause()
    }

    open fun onGamePaused() {
        stopCountdown()
        stopGameTimer()
    }

    open fun onGameResumed() {
        resumeCountdown()
        resumeGameTimer()
    }

    open fun onGameStarted() {

    }

    open fun onWordsClick() {

    }

    open fun onUserInputChanged() {
        if (_userInput.value == _currentNumber.value) {
            _score.value = (_score.value ?: 0) + 1
            MediaPlayer.create(getApplication(), R.raw.success).start()
            minNumberLength = min(9, (_score.value ?: 0) + 1)
            maxNumberLength = minNumberLength
            startNewRound()
        }
    }

    open fun onCurrentNumberChanged() {

    }

    open fun onMistakeStatusChanged(isMistaken: Boolean) {

    }

    fun setWords(animate: Boolean, newWords: SpannableStringBuilder) {
        words.value = Pair(animate, newWords)
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
            _currentNumber.value?.let { number ->
                onMistakeStatusChanged(!number.startsWith(prefix))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameTimer.pause()
    }
}