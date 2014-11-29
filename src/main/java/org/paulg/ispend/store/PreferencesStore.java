package org.paulg.ispend.store;

import org.paulg.ispend.view.dashboard.BudgetEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class PreferencesStore {

    public static final String NO_FILE = "NoFile";
    private static final String LOADED_FILE = "LoadedFile";
    private static final String SAVED_QUERY = "SavedQuery";
    private static final String SAVED_SEARCH_QUERIES = "SavedSearchQueries";
    private static final String SAVED_LABELS = "SavedLabels";

    private final Preferences prefs;
    private File workspace;

    public PreferencesStore() {
        prefs = Preferences.userRoot().node(Preferences.class.getName());
    }

    private void saveValues(Stream<String> values, String field) {
        prefs.put(field, values.collect(joining(";")));
    }

    public boolean hasSavedQueries() {
        return prefs.get(SAVED_SEARCH_QUERIES, null) != null;
    }

    public List<String> getSavedLabels() {
        return getInner(SAVED_LABELS);
    }

    public List<String> getSavedQueries() {
        return getInner(SAVED_SEARCH_QUERIES);
    }

    private List<String> getInner(String field) {
        String mergedQueries = prefs.get(field, null);
        if (mergedQueries == null)
            return new ArrayList<>();
        return Arrays.asList(mergedQueries.split(";")).stream().
                filter(x -> !x.isEmpty()).
                collect(toList());
    }

    public void saveQuery(final String query) {
        prefs.put(SAVED_QUERY, query);
    }

    public void saveLoadedFile(final String absolutePath) {
        prefs.put(LOADED_FILE, absolutePath);
    }

    public boolean hasLoadedFile() {
        return prefs.get(LOADED_FILE, null) != null;
    }

    public String getLoadedFile() {
        return prefs.get(LOADED_FILE, null);
    }

    public boolean hasQuery() {
        return prefs.get(SAVED_QUERY, null) != null;
    }

    public String getQuery() {
        return prefs.get(SAVED_QUERY, null);
    }

    public void clearAll() {
        prefs.remove(SAVED_QUERY);
        prefs.remove(LOADED_FILE);
    }

    public void saveBudgetEntries(List<BudgetEntry> budgets) {
        saveValues(budgets.stream().map(BudgetEntry::getGroup), SAVED_SEARCH_QUERIES);
        saveValues(budgets.stream().map(BudgetEntry::getLabel), SAVED_LABELS);
    }

    public String getWorkspace() {
        return prefs.get("workspace", null);
    }

    public void saveWorkspace(String absolutePath) {
        prefs.put("workspace", absolutePath);
    }
}
