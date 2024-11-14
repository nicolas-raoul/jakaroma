package fr.free.nrw.jakaroma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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

        assertEquals("pyu-tofukujaga-", k2r.convert(strs[0]));
        assertEquals("ma-jan", k2r.convert(strs[1]));
        assertEquals("tanyaotoitoidoradora", k2r.convert(strs[2]));
        assertEquals("kippu", k2r.convert(strs[3]));
        assertEquals("purikyua", k2r.convert(strs[4]));
        assertEquals("sha-pen", k2r.convert(strs[5]));
        assertEquals("kapuッ", k2r.convert(strs[6]));
        assertEquals("@ma-ku", k2r.convert(strs[7]));
        assertEquals("ti-kappu", k2r.convert(strs[8]));
        assertEquals("birudingu", k2r.convert(strs[9]));
        assertEquals("roppongihiruzu", k2r.convert(strs[10]));
        assertEquals("tottsi", k2r.convert(strs[11]));
    }
}
