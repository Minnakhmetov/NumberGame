package com.example.numbersgame.utils

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import com.example.numbersgame.R

class NumberReader {
    private val handler = Handler()
    private var mediaPlayerList = listOf<MediaPlayer>()
    private val delayList = mutableListOf<Int>()
    private var currentTrack: MediaPlayer? = null

    companion object {
        private const val SHORT_DELAY = 50
        private const val LONG_DELAY = 250
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        currentTrack?.pause()
    }

    fun load(context: Context, number: String) {
        stop()

        if (number.length !in 1..9)
            return

        val rawList = mutableListOf<Int>()
        delayList.clear()

        if (number == "0") {
            rawList.add(R.raw.a0)
        } else {
            val fullNumber = "0".repeat(9 - number.length) + number
            val chunks = fullNumber.chunked(3)

            for (i in 0..2) {
                val ch = chunks[i]

                if (ch == "000")
                    continue

                if (ch[0] == '0' || ch.count { it != '0' } == 1) {
                    rawList.add(context.getRawId("a${ch.removeLeadingZeroes()}" + "000".repeat(2 - i)))
                    delayList.add(LONG_DELAY)
                }
                else {
                    rawList.add(context.getRawId("a${ch[0]}00and"))
                    delayList.add(SHORT_DELAY)
                    rawList.add(context.getRawId("a${ch.substring(1).removeLeadingZeroes()}" + "000".repeat(2 - i)))
                    delayList.add(LONG_DELAY)
                }
            }
        }

        mediaPlayerList = rawList.map { MediaPlayer.create(context, it) }
    }

    fun start() {
        stop()

        var lastItemEnd = 0L
        for (i in mediaPlayerList.indices) {
            val item = mediaPlayerList[i]

            handler.postDelayed({
                currentTrack = item
                item.start()
            }, lastItemEnd)

            lastItemEnd += item.duration + delayList[i]
        }
    }
}

private fun Context.getRawId(name: String): Int {
    return resources.getIdentifier(
        name,
        "raw",
        packageName
    )
}

private fun String.removeLeadingZeroes(): String {
    return substring(indexOfFirst { it != '0' })
}