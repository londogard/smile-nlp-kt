package com.londogard.smile

import com.londogard.smile.extensions.StopWordFilter
import com.londogard.smile.extensions.sentences
import com.londogard.smile.extensions.words
import smile.math.Math
import smile.nlp.NGram
import smile.nlp.SimpleCorpus
import smile.nlp.collocation.BigramCollocation
import smile.nlp.collocation.BigramCollocationFinder
import smile.nlp.pos.HMMPOSTagger
import smile.nlp.pos.PennTreebankPOS
import kotlin.math.log10
import kotlin.math.max
/**
 * A interface that supplies methods to create different NLP structures out of text & more.
 */
interface SmileOperators {

    /**
     * This method creates a [SimpleCorpus]. [SimpleCorpus] is IN_MEMORY meaning that we can only handle smaller datasets.
     * [SimpleCorpus] contains handy methods for stuffs such as Bigrams, Frequency etc.
     *
     * @param text the different documents in a list
     * @return the [SimpleCorpus] generated from the documents supplied
     */
    fun corpus(text: List<String>): SimpleCorpus {
        val corpus = SimpleCorpus()
        text.forEachIndexed { index, s -> corpus.add(index.toString(), "", s) }
        return corpus
    }

    /**
     * @param k the top k bigram collocations in the given corpus.
     * @param minFreq the minimum frequency of collocation.
     * @param text is a list of documents
     * @return the top k bigrams.
     */
    fun bigram(k: Int, minFreq: Int, text: List<String>): Array<BigramCollocation> {
        val finder = BigramCollocationFinder(minFreq)
        return finder.find(corpus(text), k)
    }

    /**
     * @param p the p-value threshold
     * @return the array of significant bigram collocations in descending order
     * of likelihood ratio.
     */
    fun bigram(p: Double, minFreq: Int, text: List<String>): Array<BigramCollocation> {
        val finder = BigramCollocationFinder(minFreq)
        return finder.find(corpus(text), p)
    }

    /**
     * @param maxNGramSize is the maximum size of N in NGram
     * @param minFreq is The minimum frequency of n-gram in the sentences.
     * @param text is the documents
     * @return the List of Ngrams that fulfills requirement, for each document.
     */
    fun ngram(maxNGramSize: Int, minFreq: Int, text: List<String>): List<List<NGram>> {
        val sentences = text
            .flatMap { it.sentences()
                .map { sentence -> sentence.words(StopWordFilter.NONE)
                    .map { word -> SmileSingleton.porter.stripPluralParticiple(word).toLowerCase() }
                    .toTypedArray() }
            }
        val ngrams = SmileSingleton.phrase.extract(sentences, maxNGramSize, minFreq)

        return ngrams
    }

    fun postag(sentence: List<String>): Array<PennTreebankPOS> =
        HMMPOSTagger.getDefault().tag(sentence.toTypedArray())

    fun vectorize(terms: List<String>, bag: Map<String, Int>): List<Double> =
        terms.map { bag.getOrDefault(it, 0).toDouble() }

    fun vectorize(terms: List<String>, bag: Set<String>): List<Int> =
        terms.mapIndexed { index, s -> index to s }
            .filter { (_, s) -> bag.contains(s) }
            .map { (idx, _) -> idx }

    fun df(terms: List<String>, corpus: List<Map<String, Int>>): List<Int> =
        terms.map { term -> corpus.filter { it.contains(term) }.size }

    fun tfidf(tf: Double, maxtf: Double, n: Int, df: Int): Double =
        (tf / max(1.0, maxtf)) * log10((1.0 + n) / (1.0 + df))

    fun tfidf(corpus: List<List<Double>>): List<List<Double>> {
        val n = corpus.size
        val df = IntArray(corpus[0].size)
        corpus.forEach { bag -> for (i in df.indices) {
            if(bag[i] > 0) df[i] = df[i] + 1
        }
        }
        return corpus.map { bag -> tfidf(bag, n, df) }
    }

    fun tfidf(bag: List<Double>, n: Int, df: IntArray): List<Double> {
        val maxtf = bag.max() ?: 0.0
        val features =  DoubleArray(bag.size)
        for (i in features.indices) {
            features[i] = tfidf(bag[i], maxtf, n, df[i])
        }
        val norm = Math.norm(features)
        for (i in features.indices) {
            if (norm > 0) features[i] = features[i] / norm
        }
        return features.toList()
    }
}
