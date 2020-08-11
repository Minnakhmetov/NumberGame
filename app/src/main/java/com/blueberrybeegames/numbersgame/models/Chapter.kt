package com.blueberrybeegames.numbersgame.models

import com.blueberrybeegames.numbersgame.storage.RecordsStorage

data class Chapter(
    val chapterId: String,
    val name: String,
    val userScore: Int,
    val category: String
) {
    constructor(chapterId: String, name: String, category: String, recordsStorage: RecordsStorage) :
            this(
                chapterId = chapterId,
                name = name,
                userScore = recordsStorage.getRecord(chapterId),
                category = category
            )
}