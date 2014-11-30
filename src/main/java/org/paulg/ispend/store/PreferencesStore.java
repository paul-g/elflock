package org.paulg.ispend.store;

import com.google.gson.Gson;
import org.paulg.ispend.view.dashboard.BudgetEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class PreferencesStore {

    private static final String LOADED_FILE = "LoadedFile";
    private static final String SAVED_QUERY = "SavedQuery";
    private static final String SAVED_SEARCH_QUERIES = "SavedSearchQueries";
    private static final String SAVED_LABELS = "SavedLabels";

    private Map<String, List<String>> valuesMap = new HashMap<>();

    private transient final Preferences prefs;
    private transient String workspace;

    public PreferencesStore() {
        prefs = Preferences.userRoot().node(Preferences.class.getName());
        this.workspace = prefs.get("workspace", null);
    }

    private void saveValues(List<String> values, String field) {
        valuesMap.put(field, values);
        prefs.put(field, values.stream().collect(joining(";")));
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        saveValues(budgets.stream().map(BudgetEntry::getGroup).collect(toList()), SAVED_SEARCH_QUERIES);
        saveValues(budgets.stream().map(BudgetEntry::getLabel).collect(toList()), SAVED_LABELS);
    }

    public String getWorkspace() {
        return workspace;
    }

    public void saveWorkspace(String absolutePath) {
        this.workspace = absolutePath;
        prefs.put("workspace", absolutePath);
    }

    void save() throws IOException {
        Gson gson = new Gson();
        Path p = Paths.get(workspace, "config.json");
        BufferedWriter br = Files.newBufferedWriter(p, Charset.defaultCharset());
        br.write(gson.toJson(this));
        br.close();
    }
}
