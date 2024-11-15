package fr.free.nrw.jakaroma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class JakaromaTest {

    private final Jakaroma instance = new Jakaroma();

    @ParameterizedTest
    @CsvSource({
        "もらった, Moratta",
        "ホッ, Ho!",
        "すごっ, Sug!",
        "ピッザ, Pizza"
    })
    public void translate(String in, String out) {
        assertEquals(out, instance.convert(in, false, true));
    }

    @ParameterizedTest
    @CsvSource({
        "cat, Cat",
        "dog, Dog",
        "Hello!, Hello!"
    })
    public void noTranslateForLatin(String in, String out) {
        assertEquals(out, instance.convert(in, false, true));
    }

    @ParameterizedTest
    @CsvSource({
        "小学校, Shougakkou",
        "昨夜, Sakuya",
        "証書, Shousho",
        "正直, Shoujiki",
        "三日間, 三Nichikan",
        "順調, Junchou",
        "任意, Nini",
        "爪, Tsume",
        "みっかかん, Mikkakan",
        "さっぱり, Sappari",
        "ばっちり, BacchiRi",
        "じゅんちょうに, JunChouNi",
        "ジャガイモ, Jagaimo",
        "ニンジン, Ninjin",
        "ワーフル, Wa-furu",
        "ウェファ, Uェfuァ",
        "フィリピン, Firipin",
        "プライバシー, Puraibashi-",
        "バッチリ, Bacchiri",
        "ジュンチャン, JunChan"
    })
    public void yetAnotherTranslate(String in, String out) {
        assertEquals(out, instance.convert(in, false, true));
    }

    @ParameterizedTest
    @CsvSource({
        "祐介さんのモットーは「ｙｅｓ，　ｗｅ　ｃａｎ」と「ウィンブレドンや全日本大会で男女両方ともが勝つ」, 'Yuusuke San No Motto- Ha \"Yes , We  Can \"To \"Uィnburedon Ya Zennihon Taikai De Danjo Ryouhou Tomo Ga Katsu \"'",
        "。；「」, .;\"\""
    })
    public void andYetAnotherTranslate(String in, String out) {
        assertEquals(out, instance.convert(in, true, true));
    }

    @Test
    public void translateFullWidthToHalfWidth() {
        assertEquals("wifi", instance.convert("ｗｉｆｉ", false, false));
    }

    @Test
    public void translateOsmWay971134980() {
        // see https://www.openstreetmap.org/way/971134980
        assertEquals("Kizzusuku-ruKakogawaTsubaMeHoikuen", instance.convert("キッッズスクール加古川つばめ保育園", false, true));
    }
}
