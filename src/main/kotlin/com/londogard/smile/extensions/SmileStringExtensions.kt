package com.londogard.smile.extensions

import com.londogard.smile.SmileSingleton
import smile.nlp.NGram
import smile.nlp.dictionary.EnglishPunctuations
import smile.nlp.dictionary.EnglishStopWords
import smile.nlp.dictionary.StopWords
import smile.nlp.keyword.CooccurrenceKeywords
import smile.nlp.normalizer.SimpleNormalizer
import smile.nlp.pos.HMMPOSTagger
import smile.nlp.pos.PennTreebankPOS
import smile.nlp.stemmer.PorterStemmer
import smile.nlp.stemmer.Stemmer
import smile.nlp.tokenizer.SimpleSentenceSplitter
import java.util.*

/**
 * This file includes a ton of extensions on top of the String class. These helps using the Smile NLP libray.
 */

enum class StopWordFilter(val customFilter: String = "") {
    DEFAULT, COMPREHENSIVE, GOOGLE, MYSQL, NONE,
    CUSTOM // CUSTOM is a comma-separated list of stop-words
}

fun String.normalize(): String = SimpleNormalizer.getInstance().normalize(this)
fun String.sentences(): List<String> = SimpleSentenceSplitter.getInstance().split(this).toList()
fun String.words(filter: StopWordFilter = StopWordFilter.DEFAULT): List<String> {
    val tokens = SmileSingleton.simpleTokenizer.split(this).toList()

    if (filter == StopWordFilter.NONE) return tokens

    val dict = when (filter) {
        StopWordFilter.DEFAULT -> EnglishStopWords.DEFAULT
        StopWordFilter.COMPREHENSIVE -> EnglishStopWords.COMPREHENSIVE
        StopWordFilter.GOOGLE -> EnglishStopWords.GOOGLE
        StopWordFilter.MYSQL -> EnglishStopWords.MYSQL
        StopWordFilter.CUSTOM -> object : StopWords {
            val dict = filter.customFilter.split(",").toSet()

            override fun contains(word: String): Boolean = dict.contains(word)

            override fun size(): Int = dict.size

            override fun iterator(): MutableIterator<String> = dict.iterator() as MutableIterator<String>
        }
        else -> throw IllegalArgumentException("Filter $filter is not known. Please use DEFAULT, COMPREHENSIVE, GOOGLE, MYSQL, NONE or CUSTOM")
    }

    val punctuations = EnglishPunctuations.getInstance()

    return tokens.filter { word -> !(dict.contains(word.toLowerCase()) || punctuations.contains(word)) }
}

fun String.bag(filter: StopWordFilter = StopWordFilter.DEFAULT, stemmer: Stemmer? = PorterStemmer()): Map<String, Int> {
    val words = this.normalize().sentences().flatMap { it.words(filter) }
    val tokens = stemmer
        ?.let { stem -> words.map(stem::stem) }
        ?: words
    return tokens
        .map(String::toLowerCase)
        .groupBy { it }
        .mapValues { (_, v) -> v.size }
        .withDefault { 0 }
}

fun String.bag2(stemmer: Stemmer? = PorterStemmer()): Set<String> {
    val words = this.normalize().sentences().flatMap { it.words() }
    val tokens = stemmer
        ?.let { stem -> words.map(stem::stem) }
        ?: words

    return tokens.map(String::toLowerCase).toSet()
}

fun String.postag(): List<Pair<String, PennTreebankPOS>> {
    val words = this.words(StopWordFilter.NONE)

    return words.zip(HMMPOSTagger.getDefault().tag(words.toTypedArray()))
}

fun String.keywords(k: Int = 10): List<NGram> = CooccurrenceKeywords.of(this, k).toList()
