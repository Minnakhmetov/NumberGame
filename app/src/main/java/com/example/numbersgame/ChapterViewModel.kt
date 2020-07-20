package com.example.numbersgame

import android.app.Application
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import androidx.lifecycle.*

abstract class ChapterViewModel(application: Application) : AndroidViewModel(application) {
    private val COUNTDOWN: Long = 3000
    private val GAME_LENGTH: Long = 30000
    abstract val CHAPTER_ID: Int

    private var countDownTimer: CountDownTimer? = null
    protected var gameTimer = GameTimer()

    val BACKSPACE = 10

    private val _gameStartEvent = MutableLiveData<Event<Boolean>>()
    val gameStartEvent: LiveData<Event<Boolean>>
        get() = _gameStartEvent

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

    protected val _mistake = MutableLiveData<Boolean>()
    val mistake: LiveData<Boolean>
        get() = _mistake

    val finalScore
        get() = _score.value ?: 0

    abstract fun initializeWords(words: MediatorLiveData<Pair<Boolean, SpannableStringBuilder>>)

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
                _gameStartEvent.value = Event(true)
                countDownTimer = null
            }
        }

        countDownTimer?.start()
    }

    open fun onGameFinish() {
        RecordsStorage(getApplication()).saveRecord(CHAPTER_ID, _score.value ?: 0)
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
        initializeWords(words)
        startGameTimer()
        startNewRound()
    }

    protected open fun startNewRound() {
        _currentNumber.value = getRandomNumber()
        _userInput.value = ""
    }

    fun startGameTimer() {
        gameTimer.start(GAME_LENGTH)
    }

    fun stopGameTimer() {
        gameTimer.pause()
    }

    abstract fun handleButtonClick(id: Int)

    override fun onCleared() {
        super.onCleared()
        gameTimer.pause()
    }
}