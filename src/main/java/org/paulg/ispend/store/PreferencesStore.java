package org.paulg.ispend.store;

import javafx.collections.ObservableList;
import org.paulg.ispend.view.dashboard.BudgetEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
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

    public PreferencesStore() {
        prefs = Preferences.userRoot().node(Preferences.class.getName());
    }

    private void saveSearchQueries(final Stream<String> queries) {
        saveInner(queries, SAVED_SEARCH_QUERIES);
    }

    private void saveLabels(final Stream<String> labels) {
        saveInner(labels, SAVED_LABELS);
    }

    private void saveInner(Stream<String> values, String field) {
        prefs.put(field, values.collect(Collectors.joining(";")));
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
        List<String> queries = new ArrayList<>();
        if (mergedQueries == null)
            return queries;
        String[] qs = mergedQueries.split(";");
        for (String q : qs) {
            if (!q.isEmpty())
                queries.add(q);
        }
        return queries;
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

    public void clearAll() {
        prefs.remove(SAVED_QUERY);
        prefs.remove(LOADED_FILE);
    }

    public void saveBudgetEntries(List<BudgetEntry> budgets) {
        saveSearchQueries(budgets.stream().map(BudgetEntry::getGroup));
        saveLabels(budgets.stream().map(BudgetEntry::getLabel));
    }
}
