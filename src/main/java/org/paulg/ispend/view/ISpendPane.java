package org.paulg.ispend.view;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.paulg.ispend.controller.OpenHistoryHandler;
import org.paulg.ispend.main.HistoryFileVisitor;
import org.paulg.ispend.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import static javafx.collections.FXCollections.observableArrayList;

public class ISpendPane extends Observable {

    final Scene scene;
    public final ObservableList<Record> flaggedRecords = observableArrayList();
    public final ObservableList<Record> unflaggedRecords = observableArrayList();
    private final ObservableList<AggregatedRecord> groupData = observableArrayList();
    private final ObservableList<Account> accountsData = observableArrayList();
    private final SearchView searchView;
    private final Stage stage;
    private final PreferencesStore preferencesStore;

    private final AccountSummaryView accountsView;
    private final StaticVisualizer staticVisualizer;
    private final BudgetView budgetView;
    private final SearchView flaggedSearchView;
    private RecordStore recordStore;

    public ISpendPane(final Stage stage, final PreferencesStore preferencesStore) {
        this.stage = stage;
        this.preferencesStore = preferencesStore;
        this.staticVisualizer = new StaticVisualizer();
        this.searchView = new SearchView(unflaggedRecords);
        this.flaggedSearchView = new SearchView(flaggedRecords);
        this.accountsView = new AccountSummaryView(this);
        this.budgetView = new BudgetView(this);
        addObserver(budgetView);
        addObserver(accountsView);

        stage.setTitle("ISpend");

        BorderPane pane = new BorderPane();
        pane.setCenter(makeAppContent());
        pane.setId("container");
        pane.setTop(createMenuBar());

        scene = new Scene(pane);
        stage.setScene(scene);
        scene.getStylesheets().add("stylesheet.css");
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem open = new MenuItem("Open");
        open.setOnAction(new OpenHistoryHandler(this));

        MenuItem close = new MenuItem("Close");
        close.setOnAction(e -> Platform.exit());

        menu.getItems().addAll(open, close);
        menuBar.getMenus().addAll(menu);
        return menuBar;
    }

    private TabPane makeAppContent() {
        TabPane pane = new TabPane();
        pane.getTabs().add(makeDashboardTab());
        pane.getTabs().add(makeDrillDownTab("Unflagged", this.unflaggedRecords, this.searchView));
        pane.getTabs().add(makeDrillDownTab("Flagged", this.flaggedRecords, this.flaggedSearchView));
        return pane;
    }

    private Tab makeDrillDownTab(String text, final ObservableList<Record> records, SearchView searchView) {
        Tab tab = new Tab(text);
        records.addListener(new ListChangeListener<Record>() {
            @Override
            public void onChanged(Change<? extends Record> c) {
                tab.setText(text + " (" + records.size() + ")");
            }
        });
        tab.setContent(searchView);
        return tab;
    }

    private Tab makeDashboardTab() {
        Tab tab = new Tab("Dashboard");
        GridPane pane = new GridPane();
        pane.addRow(0, accountsView, staticVisualizer);
        pane.addRow(1, budgetView.getTableWidget(), budgetView.getPlotWidget());
        UiUtils.setColumnPercentWidths(pane, 40, 60);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(10, 10, 10, 10));
        tab.setContent(pane);
        return tab;
    }

    public RecordStore getRecordStore() {
        return recordStore;
    }

    public void fileSelected(final String path) throws IOException {
        final HistoryFileVisitor fileVisitor = new HistoryFileVisitor();
        recordStore = fileVisitor.getRecordStore();

        Files.walkFileTree(Paths.get(path), fileVisitor);

        searchView.setRecordStore(recordStore);

        unflaggedRecords.setAll(recordStore.getAllRecords());
        accountsData.setAll(recordStore.getAccounts());
        recordStore.printSummary();

        staticVisualizer.setEndOfMonthBalance(recordStore.getEndOfMonthBalance());

        this.setChanged();
        this.notifyObservers();
    }

    public File showDialog() {
        final DirectoryChooser chooser = new DirectoryChooser();
        File f = chooser.showDialog(stage);

        if (f != null) {
            preferencesStore.saveLoadedFile(f.getAbsolutePath());
        }

        return f;
    }

    public void show() {
        if (preferencesStore.hasLoadedFile()) {
            try {
                fileSelected(preferencesStore.getLoadedFile());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (preferencesStore.hasQuery()) {
            String query = preferencesStore.getQuery();
            setQuery(query);
        }
        stage.show();
    }

    public void clearQuery() {
        groupData.clear();
    }

    void setQuery(final String query) {
        clearQuery();
        List<AggregatedRecord> byDescription = recordStore.groupByDescription(query);
        groupData.addAll(byDescription);
        accountsData.clear();
        accountsData.addAll(recordStore.getAccounts());

    }

    public void saveQuery(String text) {
        preferencesStore.saveQuery(text);
    }

    public void saveSearchQueries(List<String> queries) {
        preferencesStore.saveSearchQueries(queries);
    }

    public List<String> getSavedSearchQueries() {
        return preferencesStore.getSavedQueries();
    }

    public List<Account> getAccounts() {
        return accountsData;
    }
}
