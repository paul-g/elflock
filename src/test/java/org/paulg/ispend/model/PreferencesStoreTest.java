package org.paulg.ispend.model;

import org.junit.*;
import org.paulg.ispend.workspace.Workspace;

public class PreferencesStoreTest {

	private Workspace store;

	@Before
	public void setUp() {
		store = new Workspace();
		store.clearAll();
	}

	@After
	public void tearDown() {
		store.clearAll();
	}


}
