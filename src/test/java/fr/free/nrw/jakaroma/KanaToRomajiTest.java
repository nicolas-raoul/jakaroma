package fr.free.nrw.jakaroma;

import org.junit.Assert;
import org.junit.Test;

public class KanaToRomajiTest {

    private final KanaToRomaji k2r = new KanaToRomaji();

    @Test
    public void translate() {
        String[] strs = {"ピュートフクジャガー",
                "マージャン",
                "タンヤオトイトイドラドラ",
                "キップ",
                "プリキュア",
                "シャーペン",
                "カプッ",
                "@マーク",
                "ティーカップ",
                "ビルディング",
                "ロッポンギヒルズ",
                "トッツィ"};

        Assert.assertEquals("pyu-tofukujaga-", k2r.convert(strs[0]));
        Assert.assertEquals("ma-jan", k2r.convert(strs[1]));
        Assert.assertEquals("tanyaotoitoidoradora", k2r.convert(strs[2]));
        Assert.assertEquals("kippu", k2r.convert(strs[3]));
        Assert.assertEquals("purikyua", k2r.convert(strs[4]));
        Assert.assertEquals("sha-pen", k2r.convert(strs[5]));
        Assert.assertEquals("kapuッ", k2r.convert(strs[6]));
        Assert.assertEquals("@ma-ku", k2r.convert(strs[7]));
        Assert.assertEquals("ti-kappu", k2r.convert(strs[8]));
        Assert.assertEquals("birudingu", k2r.convert(strs[9]));
        Assert.assertEquals("roppongihiruzu", k2r.convert(strs[10]));
        Assert.assertEquals("tottsi", k2r.convert(strs[11]));
    }
}
