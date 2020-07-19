package com.example.numbergame

import org.junit.Test

import org.junit.Assert.*

class UtilsKtTest {

    @Test
    fun testConvertThreeDigitNumberToWords() {
        val testCases = listOf<Pair<String, String>>(
            "123" to "one hundred twenty-three",
            "705" to "seven hundred five",
            "012" to "twelve",
            "001" to "one"
        )

        for (test in testCases) {
            val returnedString = convertThreeDigitNumberToWords(test.first)
            assert(returnedString == test.second) {
                "$returnedString was returned when ${test.second} was expected"
            }
        }
    }
}