package org.paulg.ispend.view;

import java.util.prefs.Preferences;

public class PreferencesStore {

	public static final String NO_FILE = "NoFile";
	private static final String LOADED_FILE = "LoadedFile";
	private static final String SAVED_QUERY = "SavedQuery";

	private final Preferences prefs;

	public PreferencesStore() {
		prefs = Preferences.userRoot().node(Preferences.class.getName());
	}

	public void saveQuery(final String query) {
		prefs.put(SAVED_QUERY, query);
	}

	public void saveLoadedFile(final String absolutePath) {
		prefs.put(LOADED_FILE, absolutePath);
	}

	public boolean hasLoadedFile() {
		String file = prefs.get(LOADED_FILE, null);
		return file != null;
	}

	public String getLoadedFile() {
		return prefs.get(LOADED_FILE, null);
	}

	public boolean hasQuery() {
		String query = prefs.get(SAVED_QUERY, null);
		return query != null;
	}

	public String getQuery() {
		return prefs.get(SAVED_QUERY, null);
	}

}
