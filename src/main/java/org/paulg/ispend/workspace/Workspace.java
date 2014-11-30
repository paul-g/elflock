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
    private transient static final String WORKSPACE = "workspace";

    private Map<String, List<String>> valuesMap = new HashMap<>();
    private transient final Path path;
    private transient final Path config;

    private Workspace(Path p) {
        this.path = p;
        this.config = path.resolve("config.json");
    }

    public List<String> getSavedLabels() {
        return  valuesMap.getOrDefault(SAVED_LABELS, new ArrayList<>());
    }

    public List<String> getSavedQueries() {
        return  valuesMap.getOrDefault(SAVED_SEARCH_QUERIES, new ArrayList<>());
    }

    public void saveBudgetEntries(List<BudgetEntry> budgets) {
        saveValues(budgets.stream().map(BudgetEntry::getGroup).collect(toList()), SAVED_SEARCH_QUERIES);
        saveValues(budgets.stream().map(BudgetEntry::getLabel).collect(toList()), SAVED_LABELS);
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
        List<String> lines = Files.readAllLines(config);
        Gson gson = new Gson();
        Workspace ps = gson.fromJson(lines.stream().collect(joining()), Workspace.class);
        this.valuesMap = ps.valuesMap;
    }

    private void save() throws IOException {
        Gson gson = new Gson();
        BufferedWriter br = Files.newBufferedWriter(config, Charset.defaultCharset());
        br.write(gson.toJson(this));
        br.close();
    }

    public String pettyPrint() {
        return valuesMap.toString();
    }

    public static Path getSavedWorkspace() {
        String path = getWorkspace();
        return path == null ? null : Paths.get(path);
    }

    public static String getWorkspace() {
        Preferences prefs = Preferences.userRoot().node(Preferences.class.getName());
        return prefs.get(WORKSPACE, null);
    }

    private static void setWorkspace(Path path) {
        Preferences prefs = Preferences.userRoot().node(Preferences.class.getName());
        prefs.put(WORKSPACE, path.toString());
    }

    public static Workspace open(Path path) throws IOException {
        Workspace wp = new Workspace(path);
        if (Files.exists(wp.config)) {
            wp.load();
        } else {
            wp.save();
        }
        setWorkspace(path);
        return wp;
    }
}
