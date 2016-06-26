package fr.free.nrw.jakaroma;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.util.List;

public class Jakaroma {

    private static boolean DEBUG = false;
    private static boolean CAPITALIZE_WORDS = true;
    
    public static void main(String[] args) {

        String input = "";

        if (args.length == 0 || args[0].length() == 0 ) {
            System.err.println("jakaroma warn: no argument passed, reading stdin...");
            System.err.flush();
            java.util.Scanner s = new java.util.Scanner(System.in).useDelimiter("\\A");
            input = ( s.hasNext() ? s.next() : "" );
        } else {
            input = args[0];
        }

        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize(input);

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
            // append all special symbols unaltered (without extra whitespaces)
            if( token.getAllFeaturesArray()[0].equals("記号") ||
                token.getAllFeaturesArray()[1].equals("サ変接続" )) {
                if( token.getSurface().equals(",") || token.getSurface().equals(".") || token.getSurface().equals(")"))
                    buffer.setLength(buffer.length() - 1); // remove the previous space
                buffer.append(token.getSurface());
                continue;
            }
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
                        
                        if ( CAPITALIZE_WORDS == true ) {
                            buffer.append(romaji.substring(0, 1).toUpperCase());
                            buffer.append(romaji.substring(1));
                        } else {
                            // Convert foreign katakana words to uppercase
                            if(token.getSurface().equals(token.getPronunciation())) // detect katakana
                                romaji = romaji.toUpperCase();
                            buffer.append(romaji);
                        }
                    }
            }
            buffer.append(" ");
        }
        System.out.println(buffer);
    }
}
