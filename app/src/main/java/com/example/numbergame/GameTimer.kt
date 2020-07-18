package com.example.numbergame

import android.os.CountDownTimer
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import kotlin.properties.Delegates

class GameTimer {
    private var stopTime by Delegates.notNull<Long>()
    private var countDownTimer: CountDownTimer? = null

    private val _secondsLeft = MutableLiveData<Long>()
    val secondsLeft: LiveData<Long>
        get() = _secondsLeft

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean>
        get() = _finished

    fun start(millis: Long) {
        stopTime = millis + SystemClock.elapsedRealtime()
        resume()
    }

    fun resume() {
        if (countDownTimer != null)
            return
        synchronized(this) {
            countDownTimer = object : CountDownTimer(stopTime - SystemClock.elapsedRealtime(), 1000) {
                override fun onFinish() {
                    _finished.value = true
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

    fun onFinishHandled() {
        _finished.value = false
    }


}