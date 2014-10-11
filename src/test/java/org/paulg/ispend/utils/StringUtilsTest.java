package org.paulg.ispend.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

public class StringUtilsTest {

	@Test
	public void testContainsIgnoreCase() {
		assertTrue("Pattern contained in string", StringUtils.containsIgnoreCase("Testabc", "tab"));
		assertFalse("Pattern not contained in string", StringUtils.containsIgnoreCase("testabc", "cd"));
	}

	@Test
	public void testTrimCharacters() {
		assertEquals("Trim trailing and leading characters", "abc", StringUtils.trimCharacters("\"abc'", "\"'"));
		assertEquals("Trim leading character", "abc", StringUtils.trimCharacters("\"abc", "\"'"));
		assertEquals("Nothing to trim", "abc", StringUtils.trimCharacters("abc", "\"'"));
		assertEquals("Trim to empty string", "", StringUtils.trimCharacters("'\"'", "\"'"));
	}
}
