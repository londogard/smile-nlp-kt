package com.londogard.smile

import com.londogard.smile.extensions.StopWordFilter
import com.londogard.smile.extensions.sentences
import com.londogard.smile.extensions.words
import smile.math.MathEx
import smile.nlp.Bigram
import smile.nlp.NGram
import smile.nlp.SimpleCorpus
import smile.nlp.Text
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
    fun corpus(text: List<String>): SimpleCorpus =
        SimpleCorpus().apply {
            text.forEach { s -> add(Text(s)) }
        }

    /**
     * @param k the top k bigram collocations in the given corpus.
     * @param minFreq the minimum frequency of collocation.
     * @param text is a list of documents
     * @return the top k bigrams.
     */
    fun bigram(k: Int, minFreq: Int, text: List<String>): Array<smile.nlp.collocation.Bigram> =
        smile.nlp.collocation.Bigram.of(corpus(text), k, minFreq)

    /**
     * @param p the p-value threshold
     * @return the array of significant bigram collocations in descending order
     * of likelihood ratio.
     */
    fun bigram(p: Double, minFreq: Int, text: List<String>): Array<smile.nlp.collocation.Bigram> =
        smile.nlp.collocation.Bigram.of(corpus(text), p, minFreq)

    /**
     * @param maxNGramSize is the maximum size of N in NGram
     * @param minFreq is The minimum frequency of n-gram in the sentences.
     * @param text is the documents
     * @return the List of Ngrams that fulfills requirement, for each document.
     */
    fun ngram(maxNGramSize: Int, minFreq: Int, text: List<String>): Array<Array<smile.nlp.collocation.NGram>> {
        val sentences = text.flatMap {
            it.sentences().map { sentence ->
                sentence
                    .words(StopWordFilter.NONE)
                    .map { word -> SmileSingleton.porter.stripPluralParticiple(word).toLowerCase() }
                    .toTypedArray()
            }
        }

        return smile.nlp.collocation.NGram.of(sentences, maxNGramSize, minFreq)
    }

    /** Part-of-speech taggers.
     *
     * @param sentence a sentence that is already segmented to words.
     * @return the pos tags.
     */
    fun postag(sentence: List<String>): Array<PennTreebankPOS> =
        HMMPOSTagger.getDefault().tag(sentence.toTypedArray())

    /** Converts a bag of words to a feature vector.
     *
     * @param terms the token list used as features.
     * @param bag the bag of words.
     * @return a vector of frequency of feature tokens in the bag.
     */
    fun vectorize(terms: List<String>, bag: Map<String, Int>): List<Double> =
        terms.map { bag.getOrDefault(it, 0).toDouble() }

    /** Converts a binary bag of words to a sparse feature vector.
     *
     * @param terms the token list used as features.
     * @param bag the bag of words.
     * @return an integer vector, which elements are the indices of presented
     *         feature tokens in ascending order.
     */
    fun vectorize(terms: List<String>, bag: Set<String>): List<Int> =
        terms.mapIndexed { index, s -> index to s }
            .filter { (_, s) -> bag.contains(s) }
            .map { (idx, _) -> idx }

    /** Returns the document frequencies, i.e. the number of documents that contain term.
     *
     * @param terms the token list used as features.
     * @param corpus the training corpus.
     * @return the list of document frequencies.
     */
    fun df(terms: List<String>, corpus: List<Map<String, Int>>): List<Int> =
        terms.map { term -> corpus.filter { it.contains(term) }.size }

    /** TF-IDF relevance score between a term and a document based on a corpus.
     *
     * @param tf    the frequency of searching term in the document to rank.
     * @param maxtf the maximum frequency over all terms in the document.
     * @param n     the number of documents in the corpus.
     * @param df    the number of documents containing the given term in the corpus.
     */
    fun tfidf(tf: Double, maxtf: Double, n: Int, df: Int): Double =
        (tf / max(1.0, maxtf)) * log10((1.0 + n) / (1.0 + df))


    /** Converts a corpus to TF-IDF feature vectors, which
     * are normalized to L2 norm 1.
     *
     * @param corpus the corpus of documents in bag-of-words representation.
     * @return a matrix of which each row is the TF-IDF feature vector.
     */
    fun tfidf(corpus: List<List<Double>>): List<List<Double>> {
        val n = corpus.size
        val df = IntArray(corpus[0].size)
        corpus.forEach { bag ->
            for (i in df.indices) {
                if (bag[i] > 0) df[i] = df[i] + 1
            }
        }
        return corpus.map { bag -> tfidf(bag, n, df) }
    }

    /** Converts a bag of words to a feature vector by TF-IDF, which
     * is normalized to L2 norm 1.
     *
     * @param bag the bag-of-words feature vector of a document.
     * @param n the number of documents in training corpus.
     * @param df the number of documents containing the given term in the corpus.
     * @return TF-IDF feature vector
     */
    fun tfidf(bag: List<Double>, n: Int, df: IntArray): List<Double> {
        val maxtf = bag.max() ?: 0.0
        val features = DoubleArray(bag.size)
        for (i in features.indices) {
            features[i] = tfidf(bag[i], maxtf, n, df[i])
        }
        MathEx.unitize(features)

        return features.toList()
    }
}
