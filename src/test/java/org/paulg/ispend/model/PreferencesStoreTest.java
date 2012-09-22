package org.paulg.ispend.model;

import static org.junit.Assert.*;

import org.junit.*;

public class PreferencesStoreTest {

	private PreferencesStore store;

	@Before
	public void setUp() {
		store = new PreferencesStore();
		store.clearAll();
	}

	@After
	public void tearDown() {
		store.clearAll();
	}

	@Test
	public void testQuery() {
		String query = "this is a simple query";
		assertFalse(store.hasQuery());
		store.saveQuery(query);
		assertTrue(store.hasQuery());
		assertEquals(query, store.getQuery());
	}

	@Test
	public void testLoadedFile() {
		String loadedFile = "/path/to/loaded/file";
		assertFalse(store.hasLoadedFile());
		store.saveLoadedFile(loadedFile);
		assertTrue(store.hasLoadedFile());
		assertEquals(loadedFile, store.getLoadedFile());
	}
}
