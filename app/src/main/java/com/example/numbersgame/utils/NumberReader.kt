package com.example.numbersgame.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import timber.log.Timber

class NumberReader(private val context: Context) {
    private val handler = Handler()
    private val pathList = mutableListOf<Uri>()
    private val delayList = mutableListOf<Long>()
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val SHORT_DELAY = 0L
        private const val LONG_DELAY = 250L
        private const val AFTER_SUCCESS_DELAY = 800L
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun load(number: String) {

        if (number.length !in 1..9)
            return

        pathList.clear()
        delayList.clear()

        if (number == "0") {
            pathList.add(context.getUri("a0"))
        } else {
            val fullNumber = "0".repeat(9 - number.length) + number
            val chunks = fullNumber.chunked(3)

            for (i in 0..2) {
                val ch = chunks[i]

                if (ch == "000")
                    continue

                if (ch[0] == '0' || ch.count { it != '0' } == 1) {
                    pathList.add(context.getUri("a${ch.removeLeadingZeroes()}" + "000".repeat(2 - i)))
                    delayList.add(LONG_DELAY)
                }
                else {
                    pathList.add(context.getUri("a${ch[0]}00and"))
                    delayList.add(SHORT_DELAY)
                    pathList.add(context.getUri("a${ch.substring(1).removeLeadingZeroes()}" + "000".repeat(2 - i)))
                    delayList.add(LONG_DELAY)
                }
            }
        }
    }

    private fun postTrack(index: Int, delay: Long) {
        handler.postDelayed({
            mediaPlayer?.run {
                reset()
                setDataSource(context, pathList[index])
                setOnPreparedListener {
                    start()
                    if (index + 1 < pathList.size) {
                        postTrack(index + 1, duration + delayList[index])
                    }
                }
                prepareAsync()
            }
        }, delay)
    }

    fun start(afterSuccess: Boolean = false) {
        stop()
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer()
        postTrack(0, if (afterSuccess) AFTER_SUCCESS_DELAY else 0)
    }
}

private fun Context.getUri(name: String) = Uri.parse("android.resource://${packageName}/raw/$name")

private fun String.removeLeadingZeroes(): String {
    return substring(indexOfFirst { it != '0' })
}