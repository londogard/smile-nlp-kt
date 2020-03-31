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
Smile is required to be installed, you can find the artifact [here](https://search.maven.org/artifact/com.github.haifengl/smile-core) (installable by gradle).

## Usage
TO BE FILLED.
