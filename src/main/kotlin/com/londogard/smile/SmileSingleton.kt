package com.londogard.smile

import smile.nlp.stemmer.LancasterStemmer
import smile.nlp.stemmer.PorterStemmer
import smile.nlp.tokenizer.SimpleTokenizer

/**
 * This is a singleton object that contains a few lazily initiated classes
 */
object SmileSingleton {
    val simpleTokenizer by lazy { SimpleTokenizer(true) }
    val porter by lazy { PorterStemmer() }
    val lancaster by lazy { LancasterStemmer() }

    fun porter(word: String): String = porter.stem(word)
    fun lancaster(word: String): String = lancaster.stem(word)
}