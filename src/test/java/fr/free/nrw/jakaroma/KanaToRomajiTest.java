package fr.free.nrw.jakaroma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class KanaToRomajiTest {

    private final KanaToRomaji k2r = new KanaToRomaji();

    @ParameterizedTest
    @CsvSource({
        "ピュートフクジャガー, pyu-tofukujaga-",
        "マージャン, ma-jan",
        "タンヤオトイトイドラドラ, tanyaotoitoidoradora",
        "キップ, kippu",
        "プリキュア, purikyua",
        "シャーペン, sha-pen",
        "カプッ, kapuッ",
        "@マーク, @ma-ku",
        "ティーカップ, ti-kappu",
        "ビルディング, birudingu",
        "ロッポンギヒルズ, roppongihiruzu",
        "トッツィ, tottsi"
    })
    public void translate(String in, String out) {
        assertEquals(out, k2r.convert(in));
    }
}
