package org.paulg.ispend.workspace;

import com.google.gson.Gson;
import org.paulg.ispend.view.dashboard.BudgetEntry;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.prefs.Preferences;

import static java.util.stream.Collectors.*;

public class Workspace {

    private transient static final String SAVED_SEARCH_QUERIES = "SavedSearchQueries";
    private transient static final String SAVED_LABELS = "SavedLabels";
    private static final String WORKSPACE = "workspace";

    private Map<String, List<String>> valuesMap = new HashMap<>();

    private transient Preferences prefs;
    private transient String workspace;

    public void init() throws IOException {
        prefs = Preferences.userRoot().node(Preferences.class.getName());
        this.workspace = prefs.get(WORKSPACE, null);
        if (workspace != null)
            load();
    }

    public List<String> getSavedLabels() {
        return  valuesMap.getOrDefault(SAVED_LABELS, null);
    }

    public List<String> getSavedQueries() {
        return  valuesMap.getOrDefault(SAVED_SEARCH_QUERIES, null);
    }

    public void clearAll() {
        prefs.remove(WORKSPACE);
        workspace = null;
    }

    public void saveBudgetEntries(List<BudgetEntry> budgets) {
        saveValues(budgets.stream().map(BudgetEntry::getGroup).collect(toList()), SAVED_SEARCH_QUERIES);
        saveValues(budgets.stream().map(BudgetEntry::getLabel).collect(toList()), SAVED_LABELS);
    }

    public String getWorkspace() {
        return workspace;
    }

    public void saveWorkspace(String absolutePath) throws IOException {
        this.workspace = absolutePath;
        prefs.put(WORKSPACE, absolutePath);
        load();
    }

    private void saveValues(List<String> values, String field) {
        valuesMap.put(field, values);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(workspace, "config.json"));
        Gson gson = new Gson();
        Workspace ps = gson.fromJson(lines.stream().collect(joining()), Workspace.class);
        this.valuesMap = ps.valuesMap;
    }

    private void save() throws IOException {
        Gson gson = new Gson();
        Path p = Paths.get(workspace, "config.json");
        BufferedWriter br = Files.newBufferedWriter(p, Charset.defaultCharset());
        br.write(gson.toJson(this));
        br.close();
    }

    public String pettyPrint() {
        return valuesMap.toString();
    }
}
