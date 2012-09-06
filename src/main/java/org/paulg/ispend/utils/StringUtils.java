package org.paulg.ispend.utils;

import java.util.Locale;

public final class StringUtils {

	private StringUtils() {
	}

	public static boolean containsIgnoreCase(final String text, final String pattern) {
		return text.toLowerCase(Locale.getDefault()).contains(pattern.toLowerCase(Locale.getDefault()));
	}

	public static String trimCharacters(final String text, final String characters) {
		int spos = 0;
		while ((spos < text.length()) && (characters.indexOf(text.charAt(spos)) >= 0)) {
			spos++;
		}
		int fpos = text.length() - 1;
		while ((fpos >= 0) && (characters.indexOf(text.charAt(fpos)) >= 0)) {
			fpos--;
		}
		if (spos <= fpos) {
			return text.substring(spos, fpos + 1);
		}
		return new String("");
	}
}