package org.paulg.ispend.utils;

public class StringUtils {

	/**
	 * 
	 * @param text
	 *            the string to search
	 * @param pattern
	 *            the pattern to search for
	 * @return
	 */
	public static boolean containsIgnoreCase(final String text, final String pattern) {
		return text.toLowerCase().contains(pattern.toLowerCase());
	}
}
