package com.blueberrybeegames.numbersgame.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Handler
import timber.log.Timber

class NumberReader(private val context: Context, private val soundPool: SoundPool) {
    private val handler = Handler()
    private var ids = listOf<Int>()
    private val delays = mutableListOf<Long>()
    private var durations = listOf<Int>()
    private var streamId = -1
    var onLoadCompleteListener: (() -> Unit)? = null
    private var loaded = 0
    var loadedNumber: String = ""

    companion object {
        private const val SHORT_DELAY = 0L
        private const val LONG_DELAY = 250L
        private const val AFTER_SUCCESS_DELAY = 800L
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        if (streamId != -1)
            soundPool.stop(streamId)
    }

    fun unload() {
        for (id in ids)
            soundPool.unload(id)
    }

    fun load(number: String) {
        loadedNumber = number

        unload()
        if (number.length !in 1..9)
            return

        delays.clear()

        val mediaPlayer = MediaPlayer()

        val trackNames = mutableListOf<String>()

        if (number == "0") {
            trackNames.add("a0")
        } else {
            val fullNumber = "0".repeat(9 - number.length) + number
            val chunks = fullNumber.chunked(3)

            for (i in 0..2) {
                val ch = chunks[i]

                if (ch == "000")
                    continue

                if (ch[0] == '0' || ch.count { it != '0' } == 1) {
                    trackNames.add("a${ch.removeLeadingZeroes()}" + "000".repeat(2 - i))
                    delays.add(LONG_DELAY)
                }
                else {
                    trackNames.add("a${ch[0]}00and")
                    delays.add(SHORT_DELAY)
                    trackNames.add("a${ch.substring(1).removeLeadingZeroes()}" + "000".repeat(2 - i))
                    delays.add(LONG_DELAY)
                }
            }
        }

        durations = trackNames.map {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://${context.packageName}/raw/$it"))
            mediaPlayer.prepare()
            mediaPlayer.duration
        }

        loaded = 0

        soundPool.setOnLoadCompleteListener { _, _, _ ->
            loaded++
            if (loaded == ids.count())
                onLoadCompleteListener?.invoke()
        }

        ids = trackNames.map {
            soundPool.load(context, context.getRawId(it), 0)
        }

        mediaPlayer.reset()
        mediaPlayer.release()
    }

    private fun postTrack(index: Int, delay: Long) {
        handler.postDelayed({
            if (index < ids.size) {
                streamId = soundPool.play(ids[index], 1F, 1F, 0, 0, 1F)
                if (index + 1 < ids.size) {
                    postTrack(index + 1, durations[index] + delays[index])
                }
            }
        }, delay)
    }

    fun start(afterSuccess: Boolean = false) {
        stop()
        if (ids.isNotEmpty())
            postTrack(0, if (afterSuccess) AFTER_SUCCESS_DELAY else 0)
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