package com.example.doriclingo.repositories

import com.example.doriclingo.model.Sentence

//object to ensure only one instance is created
object SentenceRepository {
    var sentenceList: List<Sentence> = emptyList()

    fun getSentences(translatedSentences: List<Sentence>) {
        sentenceList = translatedSentences
    }
}
