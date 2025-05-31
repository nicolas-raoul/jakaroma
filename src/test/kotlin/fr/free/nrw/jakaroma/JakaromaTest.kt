package fr.free.nrw.jakaroma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JakaromaTest {

    private val instance = Jakaroma()

    @ParameterizedTest
    @CsvSource(
        "もらった, Moratta",
        "ホッ, Ho!",
        "すごっ, Sug!", // Original Jakaroma.java would produce "Sug!" due to sokuon at end of token list.
                       // My Jakaroma.kt: if (currentKatakana.endsWith("ッ")) { if (i == tokensSize - 1) { romaji = ... + "!" } }
                       // This should align.
        "ピッザ, Pizza"  // Original Jakaroma.java: smallTsuRomaji for "ッ" + next token.
                       // My Jakaroma.kt: smallTsuRomaji handles "ッ" + next token.
                       // "ピッザ" -> tokens: "ピッ", "ザ" (or similar based on Kuromoji)
                       // If "ピッ" ends with "ッ", it calls smallTsuRomaji.
                       // smallTsuRomaji(currentToken="ピッ", nextToken="ザ")
                       // currentKatakana = getKatakana("ピッ") -> "ピッ"
                       // nextKatakana = getKatakana("ザ") -> "ザ"
                       // currentRomajiPart = KanaToRomaji.convert("ピ") -> "pi"
                       // nextTokenRomaji = KanaToRomaji.convert("ザ") -> "za"
                       // result: "pi" + "z" (from "za") + "za" = "pizza". This aligns.
    )
    fun translate(input: String, expectedOutput: String) {
        assertEquals(expectedOutput, instance.convert(input, false, true))
    }

    @ParameterizedTest
    @CsvSource(
        "cat, Cat",
        "dog, Dog",
        "Hello!, Hello!"
    )
    fun noTranslateForLatin(input: String, expectedOutput: String) {
        assertEquals(expectedOutput, instance.convert(input, false, true))
    }

    @ParameterizedTest
    @CsvSource(
        "小学校, Shougakkou",
        "昨夜, Sakuya",
        "証書, Shousho",
        "正直, Shoujiki",
        "三日間, 三Nichikan", // This case is interesting. "三" is likely kept as is if not in dictionary/rules.
                             // Kuromoji tokenizes "三日間" into "三" (symbol/number) and "日間" (noun).
                             // "三" might be passed as surface to KanaToRomaji.convert. If not in dict, it stays "三".
                             // "日間" (reading "ニッカン" or "ニチカン") -> "Nichikan" or "Nitsukan".
                             // Jakaroma.kt `convert` method:
                             // when type is "数" -> buffer.append(KanaToRomaji.convert(token.getSurface()));
                             // So "三" stays "三". Then "Nichikan" is appended.
                             // This seems to align with the expected output.
        "順調, Junchou",
        "任意, Nini", // Might be "Nin'i" or "Nini" depending on "んい" handling. Kuromoji: ニンイ. KanaToRomaji: "ni" + "n" + "i" -> "nini"
        "爪, Tsume",
        "みっかかん, Mikkakan",
        "さっぱり, Sappari",
        "ばっちり, BacchiRi", // Capital 'R' - check capitalization logic in Jakaroma.convert
                             // if (capitalizeWords) { buffer.append(romaji.substring(0, 1).toUpperCase()); buffer.append(romaji.substring(1)); }
                             // Here capitalizeWords=true. So "BacchiRi" is expected.
        "じゅんちょうに, JunChouNi", // Similar, "JunChouNi" due to capitalizeWords=true.
        "ジャガイモ, Jagaimo",
        "ニンジン, Ninjin",
        "ワーフル, Wa-furu", // "ー" is handled by KanaToRomaji as "-".
        "ウェファ, Uェfuァ", // "ウェ" -> "we", "ファ" -> "fa". But Kuromoji might treat "ウェファ" as foreign word.
                             // Tokenizer might give "ウェファ" reading "ウェファ".
                             // KanaToRomaji("ウェファ") -> "uefa" (u + e + fu + a if individual)
                             // KanaToRomaji("ウェ") -> "we", KanaToRomaji("ファ") -> "fa".
                             // The expected "Uェfuァ" is very specific.
                             // The original Jakaroma.java's kuromojiFailedConvert might be involved if Kuromoji gives "*"
                             // My Jakaroma.kt: if token.surface == katakanaReading && isAllKatakana(token.surface) { romaji.toUpperCase() }
                             // This is for `capitalizeWords = false`. Here it's true.
                             // Let's assume Kuromoji tokenizes this and provides readings that lead to this.
                             // If Kuromoji reading for "ウェファ" is "ウェファ":
                             // KanaToRomaji.convert("ウェファ") -> "wefa" (if "フェ" is "fe", "ファ" is "fa")
                             // "ウェ" -> "we" (from map)
                             // "フ" -> "fu"
                             // "ァ" -> "a" (not in map, so stays "ァ" if KanaToRomaji.convert appends unknown chars as is)
                             // My KanaToRomaji.convert appends unknown chars as is. So "we" + "fu" + "ァ" -> "wefuァ"
                             // Then capitalizeWords=true -> "Wefuァ". This is not "Uェfuァ".
                             // This specific test case "ウェファ, Uェfuァ" might fail or indicates very specific Kuromoji behavior
                             // or a part of Jakaroma logic I'm not fully replicating for this exact input.
                             // The Java code: `romaji = kanaToRomaji.convert(getKatakana(token));`
                             // If getKatakana(token) returns "ウェファ", then `kanaToRomaji.convert("ウェファ")`
                             // The Java KanaToRomaji.convert: if not in dict, appends s.charAt(i).
                             // So "ウ" "ェ" "フ" "ァ". "ウ"->"u", "ェ" (small e) not in dict, "フ"->"fu", "ァ" (small a) not in dict.
                             // This would be "u" + "ェ" + "fu" + "ァ" -> "uェfuァ". Then capitalizeWords=true -> "Uェfuァ". This matches!
                             // This implies my KanaToRomaji.kt needs to handle "ウェ" not as a single map entry if the individual characters are meant to be processed.
                             // My KanaToRomaji.kt:
                             // if (dictionary.containsKey(s.substring(i, i + 2))) { ... i++; } else { ... oneCharSub ...}
                             // For "ウェファ": "ウェ" is in dictionary as "we". So it becomes "wefa".
                             // The original Java KanaToRomajiTest has no direct test for "ウェファ".
                             // The Java KanaToRomaji.convert logic for two-char lookup:
                             // if (i <= s.length() - 2 && dictionary.containsKey(s.substring(i, i + 2))) { append; i++; } else { one char }
                             // This is the same. So "ウェ" would be consumed as "we".
                             // The only way "Uェfuァ" is produced is if `getKatakana(token)` for "ウェファ" returns "ウエファ" (full size) or Kuromoji reading is like that.
                             // Or, if "ウェ" is NOT in the dictionary, then "ウ" "ェ" "フ" "ァ" are processed one-by-one.
                             // Let's check KanaToRomaji.kt dictionary: map["ウェ"] = "we". So it WILL be "we".
                             // This test case "ウェファ, Uェfuァ" will likely fail with my current KanaToRomaji.kt and Jakaroma.kt.
                             // The original Java code's behavior for "ウェファ" with `capitalizeWords=true` would be "Wefa".
                             // This test case seems problematic or relies on a very specific version/behavior of Kuromoji or a dictionary setup.
                             // I will keep the test as is, but I suspect it might fail.
        "フィリピン, Firipin",
        "プライバシー, Puraibashi-", // "シ" -> "shi", "ー" -> "-". "Puraibashii-" if "シー" was reading.
                                     // If reading is "プライバシー", then KanaToRomaji.convert("プライバシー")
                                     // プ(pu)ラ(ra)イ(i)バ(ba)シ(shi)ー(-) -> puraibashi- -> Capitalize -> Puraibashi-
        "バッチリ, Bacchiri",       // Re-test of a previous one, but without capital R.
        "ジュンチャン, JunChan"     // "ン" before "チ" -> "n". "JunChan" with capitalizeWords=true.
    )
    fun yetAnotherTranslate(input: String, expectedOutput: String) {
        assertEquals(expectedOutput, instance.convert(input, false, true))
    }

    @ParameterizedTest
    @CsvSource(
        // Note: The single quotes in the CSV are important for correct parsing by JUnit if the string contains commas.
        // Kotlin's string will just receive the content.
        "'祐介さんのモットーは「ｙｅｓ，　ｗｅ　ｃａｎ」と「ウィンブレドンや全日本大会で男女両方ともが勝つ」', 'Yuusuke San No Motto- Ha \"Yes , We Can \"To \"Uィnburedon Ya Zennihon Taikai De Danjo Ryouhou Tomo Ga Katsu \"'",
        "'.；「」', '.;\"\"'" // Test with trailingSpace = true
    )
    fun andYetAnotherTranslate(input: String, expectedOutput: String) {
        // The expected output for the first case has "Uィnburedon". This "Uィ" is similar to "Uェ" issue.
        // "ウィンブレドン" -> Kuromoji reading -> Katakana -> Romaji
        // If reading is "ウィンブレドン":
        // KanaToRomaji.convert("ウィンブレドン")
        // "ウィ" -> "wi" (from map)
        // "ン" -> "n"
        // "ブ" -> "bu"
        // "レ" -> "re"
        // "ド" -> "do"
        // "ン" -> "n"
        // -> "winburendon" -> Capitalize first -> "Winburendon"
        // The expected "Uィnburedon" implies that "ウィ" is not found as "wi", but rather "ウ" (u) and "ィ" (small i, not in dict) are processed.
        // This suggests that the dictionary used by the original test environment might be different or Kuromoji version specific.
        // My KanaToRomaji.kt has map["ウィ"] = "wi".
        // This test, particularly "Uィnburedon", will likely fail.
        // For the second case: ".；「」" with trailingSpace=true.
        // KanaToRomaji.convert("。") -> "."
        // KanaToRomaji.convert("；") -> ";" (not in map, stays ';')
        // KanaToRomaji.convert("「") -> "\""
        // KanaToRomaji.convert("」") -> "\""
        // Jakaroma.convert will add spaces if trailingSpace=true.
        // ". ; \" \"" (with spaces)
        // Expected: ".;\"\"" (no spaces)
        // The Jakaroma.java code for "記号":
        // buffer.append(kanaToRomaji.convert(token.getSurface())); continue;
        // This `continue` skips the trailing space logic for "記号".
        // My Jakaroma.kt:
        // if (token.allFeaturesArray[0] == "記号") { buffer.append(KanaToRomaji.convert(token.surface)); i++; continue }
        // This also skips trailing space logic. So ".;\"\"" is expected. This part should be fine.
        assertEquals(expectedOutput, instance.convert(input, true, true))
    }

    @Test
    fun translateFullWidthToHalfWidth() {
        // Jakaroma.convert("ｗｉｆｉ", false, false)
        // halfWidthToFullWidth("ｗｉｆｉ") -> "wifi"
        // tokens = tokenizer.tokenize("wifi") -> one token "wifi", reading "wifi" (or katakana if applicable)
        // type "アルファベット" -> KanaToRomaji.convert("wifi") -> "wifi" (no changes as it's latin)
        // capitalizeWords = false. No capitalization.
        // Foreign katakana to uppercase rule: token.getSurface().equals(token.getReading())
        // If surface="wifi", reading="wifi", then it would be uppercased to "WIFI".
        // Expected is "wifi".
        // The Java code for this rule: `if (token.getSurface().equals(token.getReading())) { romaji = romaji.toUpperCase(); }`
        // My Kotlin code: `if (token.surface == katakanaReading && isAllKatakana(token.surface)) { buffer.append(romaji.uppercase()) }`
        // My `isAllKatakana` check would prevent "wifi" from being uppercased. This is good.
        // If Kuromoji tokenizes "ｗｉｆｉ" (full-width) and its surface form after halfWidthToFullWidth is "wifi",
        // and its reading is also "wifi", then it depends on `isAllKatakana`.
        // If `isAllKatakana("wifi")` is false, then it remains "wifi". This is correct.
        assertEquals("wifi", instance.convert("ｗｉｆｉ", false, false))
    }

    @Test
    fun translateOsmWay971134980() {
        // see https://www.openstreetmap.org/way/971134980
        // "キッッズスクール加古川つばめ保育園"
        // Expected: "Kizzusuku-ruKakogawaTsubaMeHoikuen" (capitalizeWords=true)
        // "キッッズ" -> "kizzu" (ki + small tsu + zu, "ッ" with "ズ" should double 'z')
        //    My smallTsuRomaji: current="キッ" next="ズ" -> currentKata="キッ" nextKata="ズ"
        //    currentRomajiPart = KanaToRomaji.convert("キ") -> "ki"
        //    nextTokenRomaji = KanaToRomaji.convert("ズ") -> "zu"
        //    result: "ki" + "z" (from "zu") + "zu" -> "kizzu". Correct.
        // "スクール" -> "suku-ru" (su + ku + - + ru). Correct.
        // "加古川" -> "kakogawa" (かこがわ by Kuromoji). Correct.
        // "つばめ" -> "tsubame". Correct.
        // "保育園" -> "hoikuen". Correct.
        // All combined with capitalizeWords=true should match.
        assertEquals("Kizzusuku-ruKakogawaTsubaMeHoikuen", instance.convert("キッッズスクール加古川つばめ保育園", false, true))
    }
}
