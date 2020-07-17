package com.example.numbergame

import android.app.Application
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import androidx.lifecycle.*
import timber.log.Timber

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val COUNTDOWN: Long = 3000
    private val GAME_LENGTH: Long = 30000

    private var countDownTimer: CountDownTimer? = null
    private var gameTimer: CountDownTimer? = null

    val BACKSPACE = 10

    private val _gameStartEvent = MutableLiveData<Boolean>()
    val gameStartEvent: LiveData<Boolean>
        get() = _gameStartEvent

    private val _gameFinishEvent = MutableLiveData<Int>()
    val gameFinishEvent: LiveData<Int>
        get() = _gameFinishEvent

    private val _secondsBeforeStart = MutableLiveData<Long>()
    val countdownString: LiveData<String> =
        Transformations.map(_secondsBeforeStart) { (it + 1).toString() }

    private val _currentInput = MutableLiveData<String>()
    val currentInput: LiveData<String>
        get() = _currentInput

    private val _currentNumber = MutableLiveData<String>()

    val words = MediatorLiveData<Pair<Boolean, SpannableStringBuilder>>()

    private val _secondsLeft = MutableLiveData<Long>()
    val timerString: LiveData<String> =
        Transformations.map(_secondsLeft) { (it + 1).toString() }

    private val _score = MutableLiveData<Int>()
    val formattedScore = Transformations.map(_score) { score ->
        application.getString(R.string.score, score)
    }

    private val _mistake = MutableLiveData<Boolean>()
    val mistake: LiveData<Boolean>
        get() = _mistake

    init {
        _score.value = 0
        _currentInput.value = ""
        words.addSource(_currentNumber) { number ->
            words.value = Pair(true, getStyledWords(number, currentInput.value ?: ""))
        }
        words.addSource(_currentInput) { input ->
            words.value = Pair(false, getStyledWords(_currentNumber.value ?: "", input))
        }
    }

    fun startCountdown() {
        stopCountdown()

        countDownTimer = object : CountDownTimer(
            _secondsBeforeStart.value?.times(1000) ?: COUNTDOWN,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _secondsBeforeStart.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _gameStartEvent.value = true
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
        if (gameTimer != null) {
            startGameTimer()
        }
    }

    fun onGameStarted() {
        _gameStartEvent.value = false
    }

    fun stopCountdown() {
        countDownTimer?.cancel()
    }

    fun startNewRound() {
        _currentNumber.value = getRandomNumber()
        _currentInput.value = ""
    }

    fun startGameTimer() {
        gameTimer = object : CountDownTimer(
            _secondsLeft.value?.times(1000) ?: GAME_LENGTH,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _secondsLeft.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _gameFinishEvent.value = _score.value
            }
        }.start()
    }

    fun stopGameTimer() {
        gameTimer?.cancel()
    }

    fun onGameFinished() {
        _gameFinishEvent.value = -1
    }

    fun handleButtonClick(id: Int) {
        when (id) {
            BACKSPACE -> {
                _currentInput.value?.let {
                    if (it.isNotEmpty()) {
                        _currentInput.value = it.substring(0, it.length - 1)
                    }
                }
            }
            else -> {
                _currentInput.value = (_currentInput.value ?: "") + id.toString()
            }
        }
        if (_currentInput.value == _currentNumber.value) {
            startNewRound()
            _score.value = (_score.value ?: 0) + 1
        }

        _currentInput.value?.let { prefix ->
            _currentNumber.value?.let { number ->
                _mistake.value = !number.startsWith(prefix)
            }
        }
    }
}