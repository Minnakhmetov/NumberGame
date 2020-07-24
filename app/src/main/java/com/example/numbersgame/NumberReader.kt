package com.example.numbersgame

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler

class NumberReader {
    private val handler = Handler()
    private var mediaPlayerList = listOf<MediaPlayer>()
    private var currentTrack: MediaPlayer? = null

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        currentTrack?.pause()
    }

    fun load(context: Context, number: String) {
        stop()

        if (number.length !in 1..9)
            return

        val rawList = mutableListOf<Int>()

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
                }
                else {
                    rawList.add(context.getRawId("a${ch[0]}00and"))
                    rawList.add(context.getRawId("a${ch.substring(1).removeLeadingZeroes()}" + "000".repeat(2 - i)))
                }
            }
        }

        mediaPlayerList = rawList.map { MediaPlayer.create(context, it) }
    }

    fun start() {
        stop()

        var lastItemStart = 0L
        for (item in mediaPlayerList) {
            item.seekTo(0)

            handler.postDelayed({
                currentTrack = item
                item.start()
            }, lastItemStart)

            lastItemStart += item.duration
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