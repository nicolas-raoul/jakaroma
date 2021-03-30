# Jakaroma
Java kanji/etc-to-romaji converter

- Java 8+
- JUnit 4.12
- kuromoji-ipadic 0.9.0

Make sure you add the dependency below to your pom.xml before building your project.
```
<dependency>
  <groupId>com.github.nicolas-raoul</groupId>
  <artifactId>jakaroma</artifactId>
  <version>1.0.0</version>
</dependency>
```

Usage:

Build a single jar file with
```
mvn clean compile assembly:single
```

```
$ ./jakaroma.sh 六本木ヒルズ森タワー
Roppongi Hiruzu Mori Tawa-
```

or just
```
java -cp "target/jakaroma-1.0.0-jar-with-dependencies.jar" fr.free.nrw.jakaroma.Jakaroma 六本木ヒルズ森タワー
Roppongi Hiruzu Mori Tawa-
```

Or you can put it into your Maven project

Powered by [Kuromoji](https://github.com/atilika/kuromoji).
