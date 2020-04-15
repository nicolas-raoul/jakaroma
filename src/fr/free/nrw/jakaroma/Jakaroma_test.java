package fr.free.nrw.jakaroma;

import fr.free.nrw.jakaroma.Jakaroma;

public class Jakaroma_test {

	private final static int TESTCHOOSER = 0;
	public static void main ( String[] args ) {
		
		switch (TESTCHOOSER) {
			case (0): {
				//"もらった",  DONE
				//"誕生", 

				String[] testInput = {  "もらった",
										"ホッ",
										//"誕生日",
										"すごっ",
										"ピッザ"
				};
				for ( String str: testInput) {
					String[] singleArray = { str };
					Jakaroma.main( singleArray );
				}
			}
		}
	}
}
