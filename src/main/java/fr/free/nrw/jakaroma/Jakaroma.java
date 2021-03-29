package fr.free.nrw.jakaroma;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.util.List;
import java.util.Scanner;

public class Jakaroma {

    private static final boolean DEBUG = false;
    private boolean isPronunciation = false;

    /**
     * for those who use it from the command line
     */
    public static void main(String... args) {
        String input;
        if (args.length == 0 || args[0].isEmpty()) {
            System.err.println("Jakaroma warn: no argument passed, reading stdin...");
            System.err.flush();

            try (Scanner scanner = new Scanner(System.in).useDelimiter("\\A")) {
                input = (scanner.hasNext() ? scanner.next() : "");
            }
        } else {
            input = args[0];
        }

        Jakaroma instance = new Jakaroma();
        instance.isPronunciation = true;
        instance.convert(input, true, true);
    }

    /**
     * Converts kanji, etc to romaji
     *
     * @param input           the string to convert
     * @param trailingSpace   add trailing space after the token
     * @param capitalizeWords start token translation with a capital letter
     * @return romaji representation
     */
    public String convert(String input, boolean trailingSpace, boolean capitalizeWords) {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(halfWidthToFullWidth(input));
        int tokensSize = tokens.size();
        StringBuilder buffer = new StringBuilder();
        KanaToRomaji kanaToRomaji = new KanaToRomaji();

        // Display tokens
        if (DEBUG) {
            tokens.forEach(token -> System.out.println(token.getSurface() + "\t" + token.getAllFeatures()));
        }

        for (int i = 0; i < tokensSize; i++) {
            Token token = tokens.get(i);
            String type = token.getAllFeaturesArray()[1];
            if (DEBUG) {
                System.out.println("Token: " + token.getSurface());
                System.out.println("Type: " + type);
            }
            // keep newlines unaltered
            if (token.getAllFeaturesArray()[0].equals("記号")) {
                buffer.append(kanaToRomaji.convert(token.getSurface())); // Append surface form
                continue;
            }

            switch (type) {
                case "数": // Example: 4
                case "アルファベット": // Example: ｂ (double-width alphabet)
                case "サ変接続": // Example: , (connection symbols)
                    buffer.append(kanaToRomaji.convert(token.getSurface()));  // TODO - exception list for when this false positives
                    continue; // avoid extra whitespace after symbols
                default:
                    String romaji;
                    // Kuromoji provided no katakana?
                    if (getKatakana(token).equals("*")) {
                        // Look up all token characters against keys in Katakana map
                        // token may be composed of katakana but tripped up kuromoji
                        // Deal with small tsu
                        romaji = kuromojiFailedConvert(token, kanaToRomaji);
                    } else {  // kanji has been converted to katakana
                        // Convert katakana to romaji
                        String katakana = getKatakana(token);

                        if (DEBUG) {
                            System.out.println("Katakana: " + katakana);
                        }

                        String currentRomaji = kanaToRomaji.convert(katakana);
                        // sokuon at end of token list: exclamation mark
                        if (katakana.endsWith("ッ") && i == tokensSize - 1) {
                            int lastIndex = katakana.length() - 1;
                            katakana = katakana.substring(0, lastIndex);
                            // Remove space added in last loop
                            if (i != 0) {
                                buffer.deleteCharAt(buffer.length() - 1);
                            }
                            romaji = kanaToRomaji.convert(katakana) + "!";

                            if (DEBUG) {
                                System.out.println("Exclamation sokuon, romaji becomes: " + romaji);
                            }
                        }
                        // sokuon between tokens, merge with next token
                        else if (katakana.endsWith("ッ")) {
                            romaji = smallTsuRomaji(token, tokens.get(i + 1), kanaToRomaji);
                            if (DEBUG) {
                                System.out.println("Sokuon detected, merged romaji :" + romaji);
                            }
                            // Skip next token since it has been processed here and merged
                            i++;
                        } else {
                            romaji = currentRomaji;
                        }
                    }
                    // Capitalization
                    if (!romaji.isEmpty()) {
                        if (capitalizeWords) {
                            buffer.append(romaji.substring(0, 1).toUpperCase());
                            buffer.append(romaji.substring(1));
                        } else {
                            // Convert foreign katakana words to uppercase
                            if (token.getSurface().equals(token.getReading())) { // detect katakana
                                romaji = romaji.toUpperCase();
                            }
                            buffer.append(romaji);
                        }
                    }
            }
            if (trailingSpace && i != tokensSize - 1) {
                buffer.append(" ");
            }
        }
        System.out.println(buffer);
        return buffer.toString();
    }

    /*
     * @param token
     */
    private String getKatakana(Token token) {
        String katakana;
        if (isPronunciation) {
            katakana = token.getPronunciation();
        } else {
            katakana = token.getReading();
        }
        return katakana;
    }

    private String smallTsuRomaji(Token token, Token nextToken, KanaToRomaji kanaToRomaji) {
        String romaji;
        String nextRomaji = kanaToRomaji.convert(getKatakana(nextToken));
        String currentRomaji = kanaToRomaji.convert(getKatakana(token).substring(0, token.getSurface().length() - 1));
        romaji = currentRomaji + nextRomaji.charAt(0) + nextRomaji;
        return romaji;
    }

    private String kuromojiFailedConvert(Token token, KanaToRomaji kanaToRomaji) {
        StringBuilder buffer = new StringBuilder();
        String surface = token.getSurface();

        if (surface.contains("ッ") && !surface.endsWith("ッ")) {
            String[] splitTokenSurface = surface.split("ッ");
            String romaji1 = kanaToRomaji.convert(splitTokenSurface[0]);
            String romaji2 = kanaToRomaji.convert(splitTokenSurface[1]);
            buffer.append(romaji1).append(romaji2.charAt(0)).append(romaji2);
        } else {
            for (char c : token.getSurface().toCharArray()) {
                // Katakana character? Convert to romaji
                if (KanaToRomaji.getDictionary().containsKey(String.valueOf(c))) {
                    buffer.append(kanaToRomaji.convert(String.valueOf(c)));
                } else {
                    // Append as is
                    buffer.append(c);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Full-angle string conversion half-corner string
     * 1, half-width characters are starting from 33 to 126 end
     * 2, the full-width character corresponding to the half-width character is from 65281 start to 65374 end
     * 3, the half corner of the space is 32. The corresponding Full-width space is 12288
     * The relationship between Half-width and Full-width is obvious, except that the character offset is 65248 (65281-33 = 65248).
     *
     * @param fullWidthStr Non-empty full-width string
     * @return Half-angle string
     */
    private String halfWidthToFullWidth(String fullWidthStr) {
        if (null == fullWidthStr || fullWidthStr.length() <= 0) {
            return "";
        }
        char[] arr = fullWidthStr.toCharArray();
        for (int i = 0; i < arr.length; ++i) {
            int charValue = arr[i];
            if (charValue >= 65281 && charValue <= 65374) {
                arr[i] = (char) (charValue - 65248);
            } else if (charValue == 12288) {
                arr[i] = (char) 32;
            }
        }
        return new String(arr);
    }
}
