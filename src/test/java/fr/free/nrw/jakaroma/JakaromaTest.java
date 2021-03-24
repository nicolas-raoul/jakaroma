package fr.free.nrw.jakaroma;

import org.junit.Assert;
import org.junit.Test;

public class JakaromaTest {

    private final Jakaroma instance = new Jakaroma();

    @Test
    public void translate() {
        String[] testInput = {"もらった", "ホッ", "すごっ", "ピッザ"};

        Assert.assertEquals("Moratta", instance.convert(testInput[0], false, true));
        Assert.assertEquals("Ho!", instance.convert(testInput[1], false, true));
        Assert.assertEquals("Sug!", instance.convert(testInput[2], false, true));
        Assert.assertEquals("Pizza", instance.convert(testInput[3], false, true));
    }

    @Test
    public void noTranslateForLatin() {
        String[] testInput = {"cat", "dog", "Hello!"};

        Assert.assertEquals("Cat", instance.convert(testInput[0], false, true));
        Assert.assertEquals("Dog", instance.convert(testInput[1], false, true));
        Assert.assertEquals("Hello!", instance.convert(testInput[2], false, true));
    }

    @Test
    public void yetAnotherTranslate() {
        String[] testInput = {"小学校", "昨夜", "証書", "正直", "三日間", "順調", "任意", "爪", "みっかかん", "さっぱり",
        "ばっちり", "じゅんちょうに", "ジャガイモ", "ニンジン", "ワーフル", "ウェファ", "フィリピン", "プライバシー", "バッチリ", "ジュンチャン",
                "祐介さんのモットーは「ｙｅｓ，　ｗｅ　ｃａｎ」と「ウィンブレドンや全日本大会で男女両方ともが勝つ」"};

        Assert.assertEquals("Shougakkou", instance.convert(testInput[0], false, true));
        Assert.assertEquals("Sakuya", instance.convert(testInput[1], false, true));
        Assert.assertEquals("Shousho", instance.convert(testInput[2], false, true));
        Assert.assertEquals("Shoujiki", instance.convert(testInput[3], false, true));
        Assert.assertEquals("三Nichikan", instance.convert(testInput[4], false, true));
        Assert.assertEquals("Junchou", instance.convert(testInput[5], false, true));
        Assert.assertEquals("Nini", instance.convert(testInput[6], false, true));
        Assert.assertEquals("Tsume", instance.convert(testInput[7], false, true));
        Assert.assertEquals("Mikkakan", instance.convert(testInput[8], false, true));
        Assert.assertEquals("Sappari", instance.convert(testInput[9], false, true));
        Assert.assertEquals("BacchiRi", instance.convert(testInput[10], false, true));
        Assert.assertEquals("JunChouNi", instance.convert(testInput[11], false, true));
        Assert.assertEquals("Jagaimo", instance.convert(testInput[12], false, true));
        Assert.assertEquals("Ninjin", instance.convert(testInput[13], false, true));
        Assert.assertEquals("Wa-furu", instance.convert(testInput[14], false, true));
        Assert.assertEquals("Uェfuァ", instance.convert(testInput[15], false, true));
        Assert.assertEquals("Fuィripin", instance.convert(testInput[16], false, true));
        Assert.assertEquals("Puraibashi-", instance.convert(testInput[17], false, true));
        Assert.assertEquals("Bacchiri", instance.convert(testInput[18], false, true));
        Assert.assertEquals("JunChan", instance.convert(testInput[19], false, true));
        Assert.assertEquals("Yuusuke San No Motto- Ha 「Yes , We  Can 」To 「Uィnburedon Ya Zennihon Taikai De Danjo Ryouhou Tomo Ga Katsu 」", instance.convert(testInput[20], true, true));
    }

    @Test
    public void translateFullWidthToHalfWidth() {
        Assert.assertEquals("wifi", instance.convert("ｗｉｆｉ", false, false));
    }
}
