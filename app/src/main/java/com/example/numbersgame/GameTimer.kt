package com.example.numbersgame

import android.os.CountDownTimer
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import kotlin.properties.Delegates

class GameTimer {
    // Time when timer stops or -1 if timer has not started
    private var stopTime: Long = -1
    private var countDownTimer: CountDownTimer? = null

    private val _secondsLeft = MutableLiveData<Long>()
    val secondsLeft: LiveData<Long>
        get() = _secondsLeft

    private val _finished = MutableLiveData<Event<Boolean>>()
    val finished: LiveData<Event<Boolean>>
        get() = _finished

    fun start(millis: Long) {
        stopTime = millis + SystemClock.elapsedRealtime()
        resume()
    }

    fun resume() {
        if (stopTime == -1L)
            return
        synchronized(this) {
            countDownTimer = object : CountDownTimer(stopTime - SystemClock.elapsedRealtime(), 1000) {
                override fun onFinish() {
                    finish()
                }

                override fun onTick(millisUntilFinished: Long) {
                    Timber.i("tick")
                    _secondsLeft.value = millisUntilFinished / 1000
                }
            }.start()
        }
    }

    fun pause() {
        synchronized(this) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    fun finish() {
        pause()
        _finished.value = Event(true)
    }
}