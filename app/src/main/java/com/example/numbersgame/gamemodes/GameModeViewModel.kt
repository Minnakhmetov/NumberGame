package com.example.numbersgame.gamemodes

import android.app.Application
import android.media.MediaPlayer
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
    private val GAME_LENGTH: Long = 40000

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
        Transformations.map(secondsBeforeStart) { it.toString() }

    protected val _userInput = MutableLiveData<String>("")
    val userInput: LiveData<String>
        get() = _userInput

    protected val currentNumber = MutableLiveData<String>()

    private val _words = MutableLiveData<Pair<Boolean, SpannableStringBuilder>>()
    val words: LiveData<Pair<Boolean, SpannableStringBuilder>> = _words

    val timerString: LiveData<String> =
        Transformations.map(gameTimer.secondsLeft) { it.toString() }

    private val _score = MutableLiveData(0)
    val formattedScore: LiveData<String> = Transformations.map(_score) { score ->
        application.getString(R.string.score, score)
    }

    private val _gameInterfaceVisibility = MutableLiveData<Boolean>(false)
    val gameInterfaceVisibility: LiveData<Boolean> = _gameInterfaceVisibility

    private val _answer = MutableLiveData<String>("")
    val answer: LiveData<String> = _answer

    private var gameFinished = false

    private val _showResults = MediatorLiveData<Event<Boolean>>().apply {
        addSource(gameTimer.finished) { event ->
            if (event.peekContent())
                finishGame()
            value = event
        }
    }

    val showResults: LiveData<Event<Boolean>> = _showResults

    protected val _mistakeFrame = MutableLiveData<Boolean>()
    val mistake: LiveData<Boolean>
        get() = _mistakeFrame


    val finalScore
        get() = _score.value ?: 0

    fun startCountdown() {
        if (gameFinished)
            return

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

    open fun onGameFinished() {

    }

    private fun resumeCountdown() {
        if (countDownTimer != null)
            startCountdown()
    }

    private fun resumeGameTimer() {
        gameTimer.resume()
    }

    private fun stopCountdown() {
        countDownTimer?.cancel()
    }

    fun startGame() {
        startGameTimer()
        startNewRound()
        onGameStarted()
        _gameStarted.value = true
        _gameInterfaceVisibility.value = true
    }

    private fun finishGame() {
        RecordsStorage(getApplication())
            .saveRecord(CHAPTER_ID, _score.value ?: 0)
        MediaPlayer.create(getApplication(), R.raw.failure).start()
        _gameInterfaceVisibility.value = false
        _answer.value =
            getApplication<GameApplication>().getString(R.string.answer, currentNumber.value)
        gameFinished = true
    }

    private fun startNewRound() {
        currentNumber.value = getRandomNumber(minNumberLength, maxNumberLength)
        Timber.i(currentNumber.value)
        onCurrentNumberChanged()
        _userInput.value = ""
        onUserInputChanged()
    }

    private fun startGameTimer() {
        gameTimer.start(GAME_LENGTH)
    }

    private fun stopGameTimer() {
        gameTimer.pause()
    }

    open fun onGamePaused() {
        stopCountdown()
        stopGameTimer()
    }

    open fun onGameResumed() {
        if (gameFinished)
            return
        resumeCountdown()
        resumeGameTimer()
    }

    open fun onGameStarted() {

    }

    open fun onWordsClick() {

    }

    open fun onUserInputChanged() {
        if (_userInput.value == currentNumber.value) {
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
        _mistakeFrame.value = isMistaken
    }

    fun setWords(animate: Boolean, newWords: SpannableStringBuilder) {
        _words.value = Pair(animate, newWords)
    }

    fun handleScreenClick() {
        if (gameFinished)
            _showResults.value = Event(true)
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
        gameTimer.pause()
    }
}