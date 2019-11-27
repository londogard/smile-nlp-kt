[![](https://jitpack.io/v/com.londogard/smile-nlp-kt.svg)](https://jitpack.io/#com.londogard/smile-nlp-kt)

# smile-nlp-kt
Kotlin extensions / Interfaces that extends the Java/Scala implementation/implicits of Smile NLP. Basically a simplification for kotlin (&amp; probably Java) users.

## Installation
### Using jitpack
Add the following to your `build.gradle`. `$version` should be equal to the version supplied by tag above.
``` 
   repositories {
        maven { url "https://jitpack.io" }
   }
   dependencies {
         implementation 'com.github.jitpack:com.londogard:smile-nlp-kt:$version'
   }
```
### Using Github Packages
Add the following to your `build.gradle`. `$version` should be equal to the version supplied by tag above.  
The part with logging into github repository is how I understand that you need to login. If you know a better way please ping me in an issue.
```
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
```
