[![](https://jitpack.io/v/com.londogard/smile-nlp-kt.svg)](https://jitpack.io/#com.londogard/smile-nlp-kt)<a href='https://ko-fi.com/O5O819SEH' target='_blank'><img height='22' style='border:0px;height:22px;' src='https://az743702.vo.msecnd.net/cdn/kofi2.png?v=2' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

# smile-nlp-kt
Kotlin extensions / Interfaces that extends the Java/Scala implementation/implicits of Smile NLP. Basically a simplification for kotlin (&amp; probably Java) users.

## Installation
<details open>
<summary><b>Jitpack</b> (the easiest)</summary>
<br>
Add the following to your <code>build.gradle</code>. <code>$version</code> should be equal to the version supplied by tag above.
<br>
<br>
<pre>
repositories {
  maven { url "https://jitpack.io" }
}
dependencies {
  implementation 'com.londogard:smile-nlp-kt:$version'
}        
</pre>
</details>
<details>
   <summary><b>GitHub Packages</b></summary>
<br>
Add the following to your <code>build.gradle</code>. <code>$version</code> should be equal to the version supplied by tag above.  
The part with logging into github repository is how I understand that you need to login. If you know a better way please ping me in an issue.
<br>
<br>
<pre>
repositories {
   maven {
     url = uri("https://maven.pkg.github.com/londogard/smile-nlp-kt")
     credentials {
         username = project.findProperty("gpr.user") ?: System.getenv("GH_USERNAME")
         password = project.findProperty("gpr.key") ?: System.getenv("GH_TOKEN")
     }
}
}
dependencies {
   implementation "com.londogard:smile-nlp-kt:$version"
}   
</pre>
</details>

**Installing Smile**  
Smile-NLP is required to be installed, you can find the artifact [here](https://search.maven.org/artifact/com.github.haifengl/smile-nlp) (installable by gradle).
As you can see in the gradle file this is used in conjunction with `2.2.2`currently, but should work with newer versions too.


## Usage
I'll go through the usage of the components, copying the structure from the [homepage](https://haifengl.github.io/nlp.html) of Smile.
Please make sure to read the official documentation for more context, I've tried to extract a short piece of text for 
each chapter but cut out a lot of text.


### Normalization
The function normalize is a simple normalizer for processing Unicode text:

- Apply Unicode normalization form NFKC.
- Strip, trim, normalize, and compress whitespace.
- Remove control and formatting characters.
- Normalize dash, double and single quotes.

```kotlin
import com.londogard.smile.extensions.*

val unicode = "“Wildlife strikes to aircraft pose a significant safety hazard and cost the aviation industry hundreds of millions of dollars each year,” department spokeswoman Meadow Bailey told the Associated Press. “Birds make up over 90 percent of strikes in the U.S., while mammal strikes are rare.”"
val text = unicode.normalize()
```

### Sentence Breaking
Smile implement an efficient rule-based sentence splitter for English.

```kotlin
val sentences = text.sentences()
```

### Word Segmentation
The method words(filter) assumes that an English text has already been segmented into sentences and splits a sentence into tokens.
This method includes a filter where we remove stop-words. Please read official docs for more info.

```kotlin
sentences.flatMap { sentence -> sentence.words() }
```

### Stemming
Stemming is a crude heuristic process that chops off the ends of words in the hope of achieving this goal correctly most of the time, and often includes the removal of derivational affixes.

```kotlin
import com.londogard.smile.SmileSingleton

SmileSingleton.porter("democratization") // "democrat"
SmileSingleton.lancaster("democratization") // "democr"
```

### Bag of Words
The bag-of-words model is a simple representation of text as the bag of its words, disregarding grammar and word order but keeping multiplicity.

The method bag(stemmer) returns the map of word to frequency. By default, the parameter stemmer use Porter's algorithm. Passing None to disable stemming. There is a similar function bag2(stemmer) that returns a binary bag of words (Set[String]). That is, presence/absence is used instead of frequencies.

```kotlin
text.bag() // Map[String, Int], e.g. mapOf("move" -> 1, "90" -> 1, ...)
```



The function vectorize(features, bag) converts a bag of words to a feature vector. The parameter features is the token list used as features in machine learning models. Generally it is not a good practice to use all tokens in the corpus as features.

To use these functions we need to extend the interface `SmileOperators` found in `com.londogard.smile`.

```kotlin
val lines = File("data/...").readLines()
val corpus = lines.map(_.bag())

val features = listOf("like", "good", "perform", "littl", "love", "bad", "best")
val bags = corpus.map { bag -> vectorize(features, bag) }
val data = tfidf(bags)
```

### Phrase / Collocation Extraction
We got some other functions called bigram where we find bigrams.

```kotlin
bigram(10, 5, lines) // Array<BigramCollocation>(("special effects", 278, 3522.38), ...)
```

When we want more we can use the ngram function instead.

```kotlin
val phrase = ngram(4, 4, text)
phrase[2] // returns all bigrams listOf(("this", "is"), ("is", "a"), ...)
phrase[3] // returns all trigrams listOf(("this", "is", "a"), ...)
```

### Keyword Extraction
Beyond finding phrases, keyword extraction is tasked with the automatic identification of terms that best describe the subject of a document, Keywords are the terms that represent the most relevant information contained in the document, i.e. characterization of the topic discussed in a document.

```kotlin
text.keywords(10) // returns a list of size 10, e.g. listOf(([storage, capacity], 11), ([machine], 197), ([think], 45),)
```
This algorithm relies on co-occurrence probability and information theory. Therefore, the article should be long enough to contain sufficient statistical signals. In other words, it won't work on short text such as tweets.

### Part-Of-Speech Tagging
A part of speech (PoS) is a category of words which have similar grammatical properties. Words that are assigned to the same part of speech generally display similar behavior in terms of syntax – they play similar roles within the grammatical structure of sentences – and sometimes in terms of morphology, in that they undergo inflection for similar properties.

```kotlin
val sentence = """When airport foreman Scott Babcock went out onto the runway at Wiley Post-Will Rogers Memorial Airport in Utqiagvik, Alaska, on Monday to clear some snow, he was surprised to find a visitor waiting for him on the asphalt: a 450-pound bearded seal chilling in the milky sunshine."""
sentence.postag() // List<Pair<String, PennTreebankPOS>>, listOf("When" to WRB, "airport" to NN, ...)
``` 

The rest of the methods supplied by Smile should be easy to use from Kotlin so they're not wrapped.

Please read more in the official documentation.