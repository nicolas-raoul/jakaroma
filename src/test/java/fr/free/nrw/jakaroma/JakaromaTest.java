package fr.free.nrw.jakaroma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JakaromaTest {

    private final Jakaroma instance = new Jakaroma();

    @Test
    public void translate() {
        String[] testInput = {"もらった", "ホッ", "すごっ", "ピッザ"};

        assertEquals("Moratta", instance.convert(testInput[0], false, true));
        assertEquals("Ho!", instance.convert(testInput[1], false, true));
        assertEquals("Sug!", instance.convert(testInput[2], false, true));
        assertEquals("Pizza", instance.convert(testInput[3], false, true));
    }

    @Test
    public void noTranslateForLatin() {
        String[] testInput = {"cat", "dog", "Hello!"};

        assertEquals("Cat", instance.convert(testInput[0], false, true));
        assertEquals("Dog", instance.convert(testInput[1], false, true));
        assertEquals("Hello!", instance.convert(testInput[2], false, true));
    }

    @Test
    public void yetAnotherTranslate() {
        String[] testInput = {"小学校", "昨夜", "証書", "正直", "三日間", "順調", "任意", "爪", "みっかかん", "さっぱり",
        "ばっちり", "じゅんちょうに", "ジャガイモ", "ニンジン", "ワーフル", "ウェファ", "フィリピン", "プライバシー", "バッチリ", "ジュンチャン",
                "祐介さんのモットーは「ｙｅｓ，　ｗｅ　ｃａｎ」と「ウィンブレドンや全日本大会で男女両方ともが勝つ」"};

        assertEquals("Shougakkou", instance.convert(testInput[0], false, true));
        assertEquals("Sakuya", instance.convert(testInput[1], false, true));
        assertEquals("Shousho", instance.convert(testInput[2], false, true));
        assertEquals("Shoujiki", instance.convert(testInput[3], false, true));
        assertEquals("三Nichikan", instance.convert(testInput[4], false, true));
        assertEquals("Junchou", instance.convert(testInput[5], false, true));
        assertEquals("Nini", instance.convert(testInput[6], false, true));
        assertEquals("Tsume", instance.convert(testInput[7], false, true));
        assertEquals("Mikkakan", instance.convert(testInput[8], false, true));
        assertEquals("Sappari", instance.convert(testInput[9], false, true));
        assertEquals("BacchiRi", instance.convert(testInput[10], false, true));
        assertEquals("JunChouNi", instance.convert(testInput[11], false, true));
        assertEquals("Jagaimo", instance.convert(testInput[12], false, true));
        assertEquals("Ninjin", instance.convert(testInput[13], false, true));
        assertEquals("Wa-furu", instance.convert(testInput[14], false, true));
        assertEquals("Uェfuァ", instance.convert(testInput[15], false, true));
        assertEquals("Firipin", instance.convert(testInput[16], false, true));
        assertEquals("Puraibashi-", instance.convert(testInput[17], false, true));
        assertEquals("Bacchiri", instance.convert(testInput[18], false, true));
        assertEquals("JunChan", instance.convert(testInput[19], false, true));
        assertEquals("Yuusuke San No Motto- Ha \"Yes , We  Can \"To \"Uィnburedon Ya Zennihon Taikai De Danjo Ryouhou Tomo Ga Katsu \"", instance.convert(testInput[20], true, true));
        assertEquals(".;\"\"", instance.convert("。；「」", true, true));
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
