package com.example.numbersgame.gamemodes

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import timber.log.Timber

abstract class CountDownTimer(private val length: Long, private val interval: Long) {

    abstract fun onTick(millisUntilFinished: Long)
    abstract fun onFinish()

    private val handler = @SuppressLint("HandlerLeak")
    object: Handler() {
        override fun handleMessage(msg: Message) {
            val numberOfIntervals = msg.arg1

            if (numberOfIntervals == 0) {
                onFinish()
            }
            else {
                onTick(numberOfIntervals * interval)
                sendMessageDelayed(obtainMessage(0, numberOfIntervals - 1, -1), interval)
            }
        }
    }

    fun start(): CountDownTimer {
        val numberOfIntervals = (length / interval).toInt()

        if (numberOfIntervals == 0) {
            onFinish()
        }
        else {
            handler.sendMessage(handler.obtainMessage(0, numberOfIntervals, -1))
        }

        return this
    }

    fun cancel() {
        handler.removeCallbacksAndMessages(null)
    }
}