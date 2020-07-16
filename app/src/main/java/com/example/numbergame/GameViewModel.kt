package com.example.numbergame

import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import androidx.lifecycle.*
import timber.log.Timber

class GameViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null
    private var gameTimer: CountDownTimer? = null

    private val _gameStartEvent = MutableLiveData<Boolean>()

    val BACKSPACE = 10

    val gameStartEvent: LiveData<Boolean>
        get() = _gameStartEvent

    private val _gameFinishEvent = MutableLiveData<Boolean>()

    val gameFinishEvent: LiveData<Boolean>
        get() = _gameFinishEvent

    private val _secondsBeforeStart = MutableLiveData<Long>()

    private val _currentInput = MutableLiveData<String>()

    val currentInput: LiveData<String>
        get() = _currentInput

    private val _currentNumber = MutableLiveData<String>()

    val words = MediatorLiveData<SpannableStringBuilder>()

    val countdownString: LiveData<String> =
        Transformations.map(_secondsBeforeStart) { (it + 1).toString() }

    private val _secondsLeft = MutableLiveData<Long>()

    val timerString: LiveData<String> =
        Transformations.map(_secondsLeft) { it.toString() }

    init {
        _currentInput.value = ""
        words.addSource(_currentNumber) { number ->
            words.value = getStyledWords(number, currentInput.value ?: "")
        }
        words.addSource(_currentInput) { input ->
            words.value = getStyledWords(_currentNumber.value ?: "", input)
        }
    }

    fun startCountdown() {
        stopCountdown()

        countDownTimer = object : CountDownTimer(
            _secondsBeforeStart.value?.times(1000) ?: 3000,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _secondsBeforeStart.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _gameStartEvent.value = true
            }
        }

        countDownTimer?.start()
    }

    fun resumeCountdown() {
        if (countDownTimer != null)
            startCountdown()
    }

    fun resumeGameTimer() {
        if (gameTimer != null)
            startGameTimer()
    }

    fun onGameStarted() {
        _gameStartEvent.value = false
    }

    fun stopCountdown() {
        countDownTimer?.cancel()
    }

    fun startNewRound() {
        _currentNumber.value = getRandomNumber()
        Timber.i(_currentNumber.value)
        _currentInput.value = ""
    }

    fun startGameTimer() {
        gameTimer = object : CountDownTimer(
            _secondsLeft.value?.times(1000) ?: 60000,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _secondsLeft.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _gameFinishEvent.value = true
            }
        }
        gameTimer?.start()
    }

    fun stopGameTimer() {
        gameTimer?.cancel()
    }

    fun onGameFinished() {
        _gameFinishEvent.value = false
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
        }
    }
}