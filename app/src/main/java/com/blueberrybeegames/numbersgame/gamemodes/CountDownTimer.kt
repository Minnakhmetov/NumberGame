package com.blueberrybeegames.numbersgame.gamemodes

import android.os.Handler
import android.os.Message
import android.os.SystemClock
import java.lang.ref.WeakReference
import kotlin.math.round

abstract class CountDownTimer(private val interval: Long) {

    abstract fun onTick(millisUntilFinished: Long)
    abstract fun onFinish()

    private var started = false

    class MyHandler(countDownTimer: CountDownTimer): Handler() {
        private val weakReferenceTimer = WeakReference(countDownTimer)

        override fun handleMessage(msg: Message) {
            val numberOfIntervals = msg.arg1

            val countDownTimer = weakReferenceTimer.get() ?: return

            if (numberOfIntervals == 0) {
                countDownTimer.onFinish()
            }
            else {
                countDownTimer.onTick(numberOfIntervals * countDownTimer.interval)
                sendMessageDelayed(obtainMessage(0, numberOfIntervals - 1, -1), countDownTimer.interval)
            }
        }
    }

    private lateinit var handler: MyHandler

    private var stopTime: Long = -1

    var length: Long = -1
        set(value) {
            stopTime = -1
            field = value
        }

    // length = -1 when timer is resumed

    fun start(): CountDownTimer {
        if (started)
            return this

        if (!::handler.isInitialized)
            handler = MyHandler(this)

        started = true

        if (stopTime == -1L) {
            if (length == -1L) {
                throw IllegalStateException("set length before starting")
            }
            stopTime = SystemClock.elapsedRealtime() + length
        }

        val numberOfIntervals = round((stopTime - SystemClock.elapsedRealtime()).toDouble() / interval).toInt()

        if (numberOfIntervals <= 0) {
            onFinish()
        }
        else {
            handler.sendMessage(handler.obtainMessage(0, numberOfIntervals, -1))
        }

        return this
    }

    fun pause() {
        if (started) {
            handler.removeCallbacksAndMessages(null)
            started = false
        }
    }
}