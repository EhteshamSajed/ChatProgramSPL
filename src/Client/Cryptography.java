package Client;

public class Cryptography {
	public enum Mode {
		NONE, ROT13, TWOLETTEREXCHANGE
	}

	//public static Mode mode = Mode.NONE;
	public static Mode mode = Mode.ROT13;

	/*public Cryptography(Mode _mode) {
		mode = _mode;
	}*/

	public static String defaultEnDecrypt(String value) {
		switch (mode) {
		case NONE:
			
			return value;			

		case ROT13:
			
			return rot13(value);

			//break;

		case TWOLETTEREXCHANGE:
			
			return twoLetterExchange(value);

			//break;

		default:
			break;
		}
		
		return value;
	}

	public static String rot13(String value) {

		char[] values = value.toCharArray();
		for (int i = 0; i < values.length; i++) {
			char letter = values[i];

			if (letter >= 'a' && letter <= 'z') {
				// Rotate lowercase letters.

				if (letter > 'm') {
					letter -= 13;
				} else {
					letter += 13;
				}
			} else if (letter >= 'A' && letter <= 'Z') {
				// Rotate uppercase letters.

				if (letter > 'M') {
					letter -= 13;
				} else {
					letter += 13;
				}
			}
			values[i] = letter;
		}
		// Convert array to a new String.
		return new String(values);
	}

	public static String twoLetterExchange(String value) {
		if (value.length() <= 2) {
			return value;
		}

		String replaceableLetters = value.substring(0, 2);

		value = value.replaceFirst(replaceableLetters,
				replaceableLetters.charAt(1) + "" + replaceableLetters.charAt(0));

		return value;
	}
}
