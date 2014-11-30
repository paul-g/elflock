package org.paulg.ispend.model;

import static org.junit.Assert.*;

import org.junit.*;
import org.paulg.ispend.store.PreferencesStore;

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


}
