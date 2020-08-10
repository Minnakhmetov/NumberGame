package com.example.numbersgame.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import androidx.core.text.isDigitsOnly
import kotlin.random.Random

class DelayedSpannableStringBuilder : SpannableStringBuilder {
    constructor() : super()
    constructor(charSequence: CharSequence) : super(charSequence)

    private val delayedColorSpans = mutableListOf<Pair<Int, Int>>()

    fun append(other: DelayedSpannableStringBuilder) {
        for (span in other.delayedColorSpans) {
            delayedColorSpans.add(Pair(span.first + length, span.second + length))
        }
        super.append(other)
    }

    fun addDelayedSpan(start: Int, end: Int) {
        delayedColorSpans.add(Pair(start, end))
    }

    fun applyDelayedSpans(color: Int) {
        for (span in delayedColorSpans) {
            setSpan(
                ForegroundColorSpan(color),
                span.first,
                span.second,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        delayedColorSpans.clear()
    }
}

fun convertOneDigitNumberToWords(number: String): String {
    if (number.length != 1) {
        throw IllegalArgumentException("string passed is not one-digit number")
    }
    return when (number) {
        "1" -> "one"
        "2" -> "two"
        "3" -> "three"
        "4" -> "four"
        "5" -> "five"
        "6" -> "six"
        "7" -> "seven"
        "8" -> "eight"
        "9" -> "nine"
        else -> ""
    }
}

fun convertTwoDigitNumberToWords(number: String): String {
    if (number.length != 2) {
        throw IllegalArgumentException("string passed is not two-digit number")
    }

    return when (number.indexOfFirst { it != '0' }) {
        1 -> convertOneDigitNumberToWords(
            number.substring(
                1
            )
        )
        0 -> when (number) {
            "10" -> "ten"
            "11" -> "eleven"
            "12" -> "twelve"
            "13" -> "thirteen"
            "14" -> "fourteen"
            "15" -> "fifteen"
            "16" -> "sixteen"
            "17" -> "seventeen"
            "18" -> "eighteen"
            "19" -> "nineteen"
            else -> {
                var result: String = when (number[0]) {
                    '2' -> "twenty"
                    '3' -> "thirty"
                    '4' -> "forty"
                    '5' -> "fifty"
                    '6' -> "sixty"
                    '7' -> "seventy"
                    '8' -> "eighty"
                    else -> "ninety"
                }
                if (number[1] != '0')
                    result = result + "-" + convertOneDigitNumberToWords(
                        number[1].toString()
                    )
                result
            }
        }
        else -> ""
    }
}

fun convertThreeDigitNumberToWords(number: String): String {
    if (number.length != 3) {
        throw IllegalArgumentException("string passed is not three-digit number")
    }

    return when (number.indexOfFirst { it != '0' }) {
        2 -> convertOneDigitNumberToWords(
            number.substring(
                2
            )
        )
        1 -> convertTwoDigitNumberToWords(
            number.substring(
                1
            )
        )
        0 -> {
            return ("${convertOneDigitNumberToWords(
                number.substring(0, 1)
            )} hundred " +
                    convertTwoDigitNumberToWords(
                        number.substring(1, 3)
                    )).trim()
        }
        else -> ""
    }
}

fun getPartName(positionFromEnd: Int): String {
    return when (positionFromEnd) {
        0 -> ""
        1 -> "thousand"
        2 -> "million"
        else -> "billion"
    }
}

fun transparentPrefixAndUnderlineWord(position: Int, part: DelayedSpannableStringBuilder) {
    var start = 0
    repeat(position) {
        start = part.indexOfAny(charArrayOf(' ', '-'), start)
        if (start == -1)
            return@repeat
        start++
    }

    if (start == -1)
        return

    var end = part.indexOfAny(charArrayOf(' ', '-'), start)

    if (end == -1)
        end = part.length
    part.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    part.addDelayedSpan(0, start)
}

fun stylePart(number: String, prefix: String?, part: DelayedSpannableStringBuilder) {
    if (number == prefix) {
        part.addDelayedSpan(0, part.length)
    } else {
        when (prefix?.commonPrefixWith(number)?.length ?: 0) {
            0 -> {
                transparentPrefixAndUnderlineWord(
                    0,
                    part
                )
            }
            1 -> {
                if (number[0] == '0') {
                    transparentPrefixAndUnderlineWord(
                        0,
                        part
                    )
                } else if (number[1] == '0' && number[2] == '0') {
                    transparentPrefixAndUnderlineWord(
                        1,
                        part
                    )
                } else if (number[1] != '0' || number[2] != '0') {
                    transparentPrefixAndUnderlineWord(
                        2,
                        part
                    )
                }
            }
            2 -> {
                var underlinedWordPos = 0

                if (number[0] != '0') {
                    if (number[1] == '0' && number[2] == '0')
                        underlinedWordPos++
                    else
                        underlinedWordPos += 2
                }

                underlinedWordPos += when (number[1]) {
                    '0', '1' -> 0
                    else -> {
                        if (number[2] == '0') 0 else 1
                    }
                }

                transparentPrefixAndUnderlineWord(
                    underlinedWordPos,
                    part
                )
            }
        }
    }
}

fun getWords(rawNumber: String, rawPrefix: String, styled: Boolean): DelayedSpannableStringBuilder {
    if (!rawNumber.isDigitsOnly()) {
        throw IllegalArgumentException("number is not a string representation of integer")
    }
    if (!rawPrefix.isDigitsOnly()) {
        throw IllegalArgumentException("prefix is not a string representation of integer")
    }

    val remainder = rawNumber.length % 3

    val number = if (remainder == 0) rawNumber else "0".repeat(3 - remainder) + rawNumber
    val prefix = if (remainder == 0) rawPrefix else "0".repeat(3 - remainder) + rawPrefix

    val chunkedPrefix = prefix.chunked(3)
    val chunkedNumber = number.chunked(3)

    var firstMistake = true

    val builder = DelayedSpannableStringBuilder()

    for (i in chunkedNumber.indices) {
        val part = DelayedSpannableStringBuilder(
            convertThreeDigitNumberToWords(chunkedNumber[i]).let {
                if (it.isNotEmpty())
                    "$it ${getPartName(
                        chunkedNumber.size - i - 1
                    )}"
                else
                    it
            }
        )

        if (styled && firstMistake) {
            stylePart(
                chunkedNumber[i],
                chunkedPrefix.getOrNull(i),
                part
            )
            if (chunkedNumber[i] != chunkedPrefix.getOrNull(i))
                firstMistake = false
        }

        if (builder.isNotEmpty())
            builder.append(" ")
        builder.append(part)
    }

    return builder
}

fun getPowerOfTen(power: Int): Int {
    var res = 1
    repeat(power) { res *= 10 }
    return res
}

fun getRandomNumberOfLength(length: Int): String {
    return Random.nextInt(
        getPowerOfTen(length - 1),
        getPowerOfTen(length)
    ).toString()
}

fun getRandomNumber(minLength: Int, maxLength: Int): String =
//    "70" + getRandomNumberOfLength(2)
    getRandomNumberOfLength(
        Random.nextInt(
            minLength,
            maxLength + 1
        )
    )