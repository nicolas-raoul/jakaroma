# Jakaroma
Java kanji/etc-to-romaji converter.

Jakaroma converts kanji and kana (katakana, hiragana) to romaji (Latin alphabet), which can be useful to make Japanese words more-or-less readable by readers who can not read Japanese. Example usage: A map app _might_ want to convert strings such as "ハレクラニ沖縄" to "Harekurani Okinawa" for users whose locale is not the Japanese language. We hope that results are _better than nothing_, but please note that many conversions are not perfect. Pull requests welcome!

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
java -cp "target/jakaroma-1.0.0-SNAPSHOT-jar-with-dependencies.jar" fr.free.nrw.jakaroma.Jakaroma 六本木ヒルズ森タワー
Roppongi Hiruzu Mori Tawa-
```

Or you can put it into your Maven project.

Powered by [Kuromoji](https://github.com/atilika/kuromoji).
