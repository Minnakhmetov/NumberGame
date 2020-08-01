package com.example.numbersgame.gamemodes

import android.os.Handler
import android.os.Message
import android.os.SystemClock
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.math.round
import kotlin.properties.Delegates

abstract class CountDownTimer(private val length: Long, private val interval: Long) {

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
    private var stopTime by Delegates.notNull<Long>()

    fun start(): CountDownTimer {
        if (started)
            return this

        started = true

        val numberOfIntervals = if (!::handler.isInitialized) {
            handler = MyHandler(this)
            stopTime = SystemClock.elapsedRealtime() + length
            (length / interval).toInt()
        }
        else {
            round((stopTime - SystemClock.elapsedRealtime()).toDouble() / interval).toInt()
        }

        if (numberOfIntervals <= 0) {
            onFinish()
        }
        else {
            handler.sendMessage(handler.obtainMessage(0, numberOfIntervals, -1))
        }

        return this
    }

    fun addTime(extraIntervals: Int) {
        pause()
        stopTime += extraIntervals * interval
        start()
    }

    fun pause() {
        if (started) {
            handler.removeCallbacksAndMessages(null)
            started = false
        }
    }
}