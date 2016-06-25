package fr.free.nrw.jakaroma;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.util.List;

public class Jakaroma {

    private static boolean DEBUG = false;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Pass Japanese string as argument");
            return;
        }

        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize(args[0]);

        if (DEBUG) {
            // Display tokens
            for (Token token : tokens) {
                System.out.println(token.getSurface() + "\t" + token.getAllFeatures());
            }
        }

        // Display romaji
        StringBuffer buffer = new StringBuffer();
        KanaToRomaji kanaToRomaji = new KanaToRomaji();
        String lastTokenToMerge = "";
        for (Token token : tokens) {
            String type = token.getAllFeaturesArray()[1];
            if (DEBUG) {
                System.out.println("Type: " + type);
            }
            switch(token.getAllFeaturesArray()[1]) {
                case "数": // Example: 4
                    buffer.append(token.getSurface());
                    break;
                case "アルファベット": // Example: ｂ (double-width alphabet)
                    buffer.append(token.getSurface());
                    break;
                case "空白": // whitespaces
                    continue; // skip multiple whitespaces
                default:
                    // kanji has been converted to katakana
                    String lastFeature = token.getAllFeaturesArray()[8];
                    if (lastFeature.equals("*")) {
                        buffer.append(token.getSurface());
                    }
                    else {
                        // Convert katakana to romaji
                        String romaji = kanaToRomaji.convert(token.getAllFeaturesArray()[8]);
                        
                        // workaround for soukon
                        if ( lastFeature.toString().endsWith("ッ") )
                        {
                            lastTokenToMerge = lastFeature;
                            continue;
                        }
                        else
                        {
                            lastTokenToMerge = "";
                        }
                        
                        // Convert foreign katakana words to uppercase
                        if(token.getSurface().equals(token.getPronunciation()))
                            romaji = romaji.toUpperCase();

                        buffer.append(romaji);
                    }
            }
            buffer.append(" ");
        }
        System.out.println(buffer);
    }
}
