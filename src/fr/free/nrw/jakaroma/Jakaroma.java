package fr.free.nrw.jakaroma;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.util.List;

public class Jakaroma {

	private static final boolean DEBUG = false;
	private static boolean CAPITALIZE_WORDS = true;
	private static String OUTPUT_MODE = "pronunciation";
	
	// OUTPUT_MODE field determines whether we output reading or pronounciation
	public static final String getKatakana ( Token token ) {
		String katakana = "";
		if (OUTPUT_MODE == "pronunciation") 
	            katakana = token.getPronunciation();
		else if (OUTPUT_MODE == "reading") 
	        	katakana = token.getReading();		
		return katakana;
	}
	
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
	   
	
	    // Display tokens
	    if (DEBUG) {
	        for (Token token : tokens) {
	    	System.out.println(token.getSurface() + "\t" + token.getAllFeatures());
	    	}
	    }
	
	    // Display romaji
	    StringBuffer buffer = new StringBuffer();
	    KanaToRomaji kanaToRomaji = new KanaToRomaji();     
	    
	    // Main loop through tokens
	    for ( int i=0; i < tokens.size(); i++) {
	    	if (DEBUG) {System.out.println("Token: " + tokens.get(i).getSurface());}
	        String type = tokens.get(i).getAllFeaturesArray()[1];
	        if (DEBUG) {
	            System.out.println("Type: " + type);
	        }
	        // keep newlines unaltered
	        if( tokens.get(i).getAllFeaturesArray()[0].equals("記号"))  {
	            buffer.append(tokens.get(i).getSurface());   	// Append surface form
	            continue;
	        }
	        
        	boolean space = true;

	        switch(tokens.get(i).getAllFeaturesArray()[1]) {
	            case "数": // Example: 4
	            case "アルファベット": // Example: ｂ (double-width alphabet)
	            case "サ変接続": // Example: , (connection symbols)
	                buffer.append(tokens.get(i).getSurface());  // TODO - exception list for when this false positives
	                continue; // avoid extra whitespace after symbols
	            default:
	            	String romaji = "";
	
	            	// Kuromoji provided no katakana?           	
	                if (getKatakana(tokens.get(i)).equals("*")) {  
		            	// Look up all token characters against keys in Katakana map
	                	// token may be composed of katakana but tripped up kuromoji
	                	// Deal with small tsu
	                	romaji = kuromojiFailedConvert(tokens.get(i));
	                }
	                // ELSE - kanji has been converted to katakana
	                else {     
	                    // Convert katakana to romaji
	                	String katakana = getKatakana(tokens.get(i));
	                	if ( DEBUG ) System.out.println("Katakana: " + katakana);
	                    String currentRomaji = kanaToRomaji.convert( katakana );
	                    String nextTokenRomaji = "";
	
	                    // sokuon at end of token list: exclamation mark
	                    if ( katakana.toString().endsWith("ッ") && i == tokens.size() - 1 )
	                    {
	                    	//System.out.println("length of katakana:" + katakana.length());
	                    	int lastIndex = katakana.length() -1;
	                        katakana = katakana.substring(0, lastIndex);
	                        // Remove space added in last loop
	                        if ( i != 0 ) buffer.deleteCharAt(buffer.length()-1);
	                        romaji = kanaToRomaji.convert(katakana) + "!";
	                        if (DEBUG) {System.out.println("Exclamation sokuon, romaji becomes: " + romaji);}
	                    }
	                    
	                    // sokuon between tokens, merge with next token
	                    else if (katakana.toString().endsWith("ッ")){ 
	                    	romaji = smallTsuRomaji(tokens.get(i), tokens.get(i+1));
	                    	if (DEBUG) {System.out.println("Sokuon detected, merged romaji :" + romaji);}
	                    	// Skip next token since it has been processed here and merged
	                    	i++;
	                    }
	                    else {
	                    	romaji = currentRomaji;
	                    }
	                    
	                }
	                
	                // Capitalization
	            	if ( romaji != "" ) {
	                    if ( CAPITALIZE_WORDS == true ) {
	                        buffer.append(romaji.substring(0, 1).toUpperCase());
	                        buffer.append(romaji.substring(1));
	                    } else {
	                        // Convert foreign katakana words to uppercase
	                        if(tokens.get(i).getSurface().equals(tokens.get(i).getReading())) // detect katakana
	                            romaji = romaji.toUpperCase();
	                        buffer.append(romaji);
	                    }
	            	}
	            }
	        
	        if ( space ) buffer.append(" ");
	        }
	    System.out.println(buffer);
	    if (DEBUG) System.out.println("----------------------------");
	    }

	public static String smallTsuRomaji ( Token token, Token nextToken) {
		System.out.println("nexToken:" + nextToken.getSurface());
	    KanaToRomaji kanaToRomaji = new KanaToRomaji();  
		String romaji = "";
		String nextRomaji = kanaToRomaji.convert(getKatakana(nextToken));
		String currentRomaji = kanaToRomaji.convert(getKatakana(token).substring(0,token.getSurface().length()-1));
		romaji = currentRomaji + nextRomaji.substring(0,1) + nextRomaji;
		return romaji;
	}
	
	public static String kuromojiFailedConvert( Token token ) {
	    KanaToRomaji kanaToRomaji = new KanaToRomaji();  
	    StringBuffer buffer = new StringBuffer();
    	String surface = token.getSurface();
    	
    	if (surface.contains("ッ") && !surface.endsWith("ッ")) {
    		String[] splitTokenSurface = surface.split("ッ");
    		String romaji1 = kanaToRomaji.convert(splitTokenSurface[0]);
    		String romaji2 = kanaToRomaji.convert(splitTokenSurface[1]);
    		buffer.append(romaji1 + romaji2.substring(0,1) + romaji2);
    	}
    	else {
        	for ( char c : token.getSurface().toCharArray() ) {
        		// Katakana character?  Convert to romaji
        		if ( kanaToRomaji.m.containsKey( String.valueOf(c) )) {
        			buffer.append( kanaToRomaji.convert(String.valueOf(c)) );
        		}
        		else {
        			// Append as is
        			buffer.append(String.valueOf(c));
        		}
        	}
    	}	
		
		return buffer.toString();
	}
}
	
	
	
	
