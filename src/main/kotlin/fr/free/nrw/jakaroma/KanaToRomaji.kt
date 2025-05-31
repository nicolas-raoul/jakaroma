package fr.free.nrw.jakaroma

object KanaToRomaji {

    private val dictionary: Map<String, String>

    init {
        val map = HashMap<String, String>()
        map["ア"] = "a"
        map["イ"] = "i"
        map["ウ"] = "u"
        map["エ"] = "e"
        map["オ"] = "o"
        map["カ"] = "ka"
        map["キ"] = "ki"
        map["ク"] = "ku"
        map["ケ"] = "ke"
        map["コ"] = "ko"
        map["サ"] = "sa"
        map["シ"] = "shi"
        map["ス"] = "su"
        map["セ"] = "se"
        map["ソ"] = "so"
        map["タ"] = "ta"
        map["チ"] = "chi"
        map["ツ"] = "tsu"
        map["テ"] = "te"
        map["ト"] = "to"
        map["ナ"] = "na"
        map["ニ"] = "ni"
        map["ヌ"] = "nu"
        map["ネ"] = "ne"
        map["ノ"] = "no"
        map["ハ"] = "ha"
        map["ヒ"] = "hi"
        map["フ"] = "fu"
        map["ヘ"] = "he"
        map["ホ"] = "ho"
        map["マ"] = "ma"
        map["ミ"] = "mi"
        map["ム"] = "mu"
        map["メ"] = "me"
        map["モ"] = "mo"
        map["ヤ"] = "ya"
        map["ユ"] = "yu"
        map["ヨ"] = "yo"
        map["ラ"] = "ra"
        map["リ"] = "ri"
        map["ル"] = "ru"
        map["レ"] = "re"
        map["ロ"] = "ro"
        map["ワ"] = "wa"
        map["ヲ"] = "wo"
        map["ン"] = "n"
        map["ガ"] = "ga"
        map["ギ"] = "gi"
        map["グ"] = "gu"
        map["ゲ"] = "ge"
        map["ゴ"] = "go"
        map["ザ"] = "za"
        map["ジ"] = "ji"
        map["ズ"] = "zu"
        map["ゼ"] = "ze"
        map["ゾ"] = "zo"
        map["ダ"] = "da"
        map["ヂ"] = "ji"
        map["ヅ"] = "zu"
        map["デ"] = "de"
        map["ド"] = "do"
        map["バ"] = "ba"
        map["ビ"] = "bi"
        map["ブ"] = "bu"
        map["ベ"] = "be"
        map["ボ"] = "bo"
        map["パ"] = "pa"
        map["ピ"] = "pi"
        map["プ"] = "pu"
        map["ペ"] = "pe"
        map["ポ"] = "po"
        map["キャ"] = "kya"
        map["キュ"] = "kyu"
        map["キョ"] = "kyo"
        map["シャ"] = "sha"
        map["シュ"] = "shu"
        map["ショ"] = "sho"
        map["チャ"] = "cha"
        map["チュ"] = "chu"
        map["チョ"] = "cho"
        map["ニャ"] = "nya"
        map["ニュ"] = "nyu"
        map["ニョ"] = "nyo"
        map["ヒャ"] = "hya"
        map["ヒュ"] = "hyu"
        map["ヒョ"] = "hyo"
        map["リャ"] = "rya"
        map["リュ"] = "ryu"
        map["リョ"] = "ryo"
        map["ギャ"] = "gya"
        map["ギュ"] = "gyu"
        map["ギョ"] = "gyo"
        map["ジャ"] = "ja"
        map["ジュ"] = "ju"
        map["ジョ"] = "jo"
        map["ティ"] = "ti"
        map["ディ"] = "di"
        map["ツィ"] = "tsi"
        map["ヂャ"] = "dya"
        map["ヂュ"] = "dyu"
        map["ヂョ"] = "dyo"
        map["ビャ"] = "bya"
        map["ビュ"] = "byu"
        map["ビョ"] = "byo"
        map["ピャ"] = "pya"
        map["ピュ"] = "pyu"
        map["ピョ"] = "pyo"
        map["ー"] = "-"
        map["チェ"] = "che"
        map["フィ"] = "fi"
        map["フェ"] = "fe"
        map["ウィ"] = "wi"
        map["ウェ"] = "we"
        map["ヴィ"] = "ⅴi"
        map["ヴェ"] = "ve"
        map["「"] = "\""
        map["」"] = "\""
        map["。"] = "."
        dictionary = map.toMap()
    }

    fun convert(s: String): String {
        val t = StringBuilder()
        var i = 0
        while (i < s.length) {
            if (i <= s.length - 2) {
                val twoCharSub = s.substring(i, i + 2)
                if (dictionary.containsKey(twoCharSub)) {
                    t.append(dictionary[twoCharSub])
                    i++
                } else {
                    val oneCharSub = s.substring(i, i + 1)
                    if (dictionary.containsKey(oneCharSub)) {
                        t.append(dictionary[oneCharSub])
                    } else if (s[i] == 'ッ') {
                        if (i + 1 < s.length) {
                            val nextChar = s.substring(i + 1, i + 2)
                            dictionary[nextChar]?.firstOrNull()?.let { t.append(it) }
                        } else {
                            // Handle 'ッ' at the end of the string, perhaps append '!' or handle as error
                             t.append(s[i]) // Or some other appropriate action
                        }
                    } else {
                        t.append(s[i])
                    }
                }
            } else {
                val oneCharSub = s.substring(i, i + 1)
                if (dictionary.containsKey(oneCharSub)) {
                    t.append(dictionary[oneCharSub])
                } else {
                    t.append(s[i])
                }
            }
            i++
        }
        return t.toString()
    }

    // In Kotlin, it's more idiomatic to access properties directly if they are public.
    // If dictionary needs to be accessed from outside, it can be made public.
    // For now, assuming it's only used internally or by Jakaroma.kt within the same package.
    // If getDictionary() is truly needed publicly, uncomment this:
    // fun getDictionary(): Map<String, String> = dictionary
}
