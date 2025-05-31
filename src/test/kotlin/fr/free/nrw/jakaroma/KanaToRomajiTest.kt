package fr.free.nrw.jakaroma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class KanaToRomajiTest {

    // In Kotlin, KanaToRomaji is an object, so we call its methods directly.
    // No instance needed: private val k2r = KanaToRomaji()

    @ParameterizedTest
    @CsvSource(
        "ピュートフクジャガー, pyu-tofukujaga-",
        "マージャン, ma-jan",
        "タンヤオトイトイドラドラ, tanyaotoitoidoradora",
        "キップ, kippu",
        "プリキュア, purikyua",
        "シャーペン, sha-pen",
        "カプッ, kapuッ", // Original test had "kapuッ", conversion logic might make this "kapu!" or "kaput" if at end.
                           // The KanaToRomaji.convert method from previous subtask handles 'ッ' at the end by appending 't'
                           // or the first char of the next romaji if not at the end.
                           // Let's verify the existing KanaToRomaji.convert for standalone "ッ" or ending "ッ".
                           // The provided Java KanaToRomaji.convert loop:
                           // else if (s.charAt(i) == 'ッ') { t.append(dictionary.get(s.substring(i + 1, i + 2)).charAt(0)); }
                           // This implies 'ッ' must be followed by something for the original Java to not throw StringIndexOutOfBounds.
                           // My Kotlin version of KanaToRomaji.convert:
                           // else if (s[i] == 'ッ') {
                           //    if (i + 1 < s.length) {
                           //        val nextChar = s.substring(i + 1, i + 2)
                           //        dictionary[nextChar]?.firstOrNull()?.let { t.append(it) }
                           //    } else { t.append(s[i]) // Or some other appropriate action -> appends 'ッ' itself }
                           // }
                           // So "カプッ" would become "kapuッ" with current KanaToRomaji.kt
                           // This matches the expected output.
        "@マーク, @ma-ku",
        "ティーカップ, ti-kappu",
        "ビルディング, birudingu",
        "ロッポンギヒルズ, roppongihiruzu",
        "トッツィ, tottsi"
    )
    fun translate(input: String, expectedOutput: String) {
        assertEquals(expectedOutput, KanaToRomaji.convert(input))
    }
}
