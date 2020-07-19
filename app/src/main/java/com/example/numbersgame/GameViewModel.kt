package com.example.numbersgame

import android.app.Application
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import androidx.lifecycle.*

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val COUNTDOWN: Long = 3000
    private val GAME_LENGTH: Long = 30000

    private var countDownTimer: CountDownTimer? = null
    private var gameTimer = GameTimer()

    val BACKSPACE = 10

    private val _gameStartEvent = MutableLiveData<Event<Boolean>>()
    val gameStartEvent: LiveData<Event<Boolean>>
        get() = _gameStartEvent

    private val secondsBeforeStart = MutableLiveData<Long>()
    val countdownString: LiveData<String> =
        Transformations.map(secondsBeforeStart) { (it + 1).toString() }

    private val _userInput = MutableLiveData<String>()
    val userInput: LiveData<String>
        get() = _userInput

    private val _currentNumber = MutableLiveData<String>()

    val words = MediatorLiveData<Pair<Boolean, SpannableStringBuilder>>()

    val timerString: LiveData<String> =
        Transformations.map(gameTimer.secondsLeft) { (it + 1).toString() }

    private val _score = MutableLiveData<Int>()
    val formattedScore = Transformations.map(_score) { score ->
        application.getString(R.string.score, score)
    }

    val gameFinishEvent = gameTimer.finished

    private val _mistake = MutableLiveData<Boolean>()
    val mistake: LiveData<Boolean>
        get() = _mistake

    val finalScore
        get() = _score.value ?: 0

    init {
        _score.value = 0
        _userInput.value = ""
        words.addSource(_currentNumber) { number ->
            words.value = Pair(true, getStyledWords(number, userInput.value ?: ""))
        }
        words.addSource(_userInput) { input ->
            words.value = Pair(false, getStyledWords(_currentNumber.value ?: "", input))
        }
    }

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

    fun startNewRound() {
        _currentNumber.value = getRandomNumber()
        _userInput.value = ""
    }

    fun startGameTimer() {
        gameTimer.start(GAME_LENGTH)
    }

    fun stopGameTimer() {
        gameTimer.pause()
    }

    fun handleButtonClick(id: Int) {
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
        if (_userInput.value == _currentNumber.value) {
            startNewRound()
            _score.value = (_score.value ?: 0) + 1
        }

        _userInput.value?.let { prefix ->
            _currentNumber.value?.let { number ->
                _mistake.value = !number.startsWith(prefix)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameTimer.pause()
    }
}