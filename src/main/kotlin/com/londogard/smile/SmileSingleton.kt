package com.londogard.smile

import smile.nlp.collocation.AprioriPhraseExtractor
import smile.nlp.keyword.CooccurrenceKeywordExtractor
import smile.nlp.stemmer.PorterStemmer
import smile.nlp.tokenizer.SimpleTokenizer

/**
 * This is a singleton object that contains a few lazily initiated classes
 */
object SmileSingleton {
    val simpleTokenizer by lazy { SimpleTokenizer(true) }
    val keywordExtractor by lazy { CooccurrenceKeywordExtractor() }
    val porter by lazy { PorterStemmer() }
    val phrase by lazy { AprioriPhraseExtractor() }
}