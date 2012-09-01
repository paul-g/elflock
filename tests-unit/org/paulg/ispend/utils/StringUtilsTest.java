package org.paulg.ispend.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testContainsIgnoreCase() {
		assertTrue(StringUtils.containsIgnoreCase("Testabc", "tab"));
		assertTrue(!StringUtils.containsIgnoreCase("testabc", "cd"));
	}
}
