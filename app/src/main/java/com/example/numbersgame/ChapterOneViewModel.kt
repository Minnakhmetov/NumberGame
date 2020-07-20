package com.example.numbersgame

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.lifecycle.*

class ChapterOneViewModel(application: Application) : ChapterViewModel(application) {
    override val CHAPTER_ID = 1


    override fun initializeWords(words: MediatorLiveData<Pair<Boolean, SpannableStringBuilder>>) {
        words.addSource(_currentNumber) { number ->
            words.value = Pair(true, getWords(number, userInput.value ?: "", true))
        }
        words.addSource(_userInput) { input ->
            words.value = Pair(false, getWords(_currentNumber.value ?: "", input, true))
        }
    }

    override fun handleButtonClick(id: Int) {
        when (id) {
            BACKSPACE -> {
                _userInput.value?.let {
                    if (it.isNotEmpty()) {
                        _userInput.value = it.substring(0, it.length - 1)
                    }
                }
            }
            else -> {
                _userInput.value = (_userInput.value ?: "") + id.toString()
            }
        }
        if (_userInput.value == _currentNumber.value) {
            startNewRound()
            _score.value = (_score.value ?: 0) + 1
        }

        _userInput.value?.let { prefix ->
            _currentNumber.value?.let { number ->
                _mistake.value = !number.startsWith(prefix)
            }
        }
    }


}