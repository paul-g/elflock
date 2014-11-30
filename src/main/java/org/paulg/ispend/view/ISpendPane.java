package org.paulg.ispend.view;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.paulg.ispend.workspace.ImportHandler;
import org.paulg.ispend.model.*;
import org.paulg.ispend.workspace.Workspace;
import org.paulg.ispend.store.RecordStore;
import org.paulg.ispend.view.dashboard.BudgetEntry;
import org.paulg.ispend.view.dashboard.DashboardTab;
import org.paulg.ispend.view.drilldown.DrilldownTab;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableHashMap;

public class ISpendPane extends Observable {

    final Scene scene;
    public final ObservableMap<String, ObservableList<Record>> flagLists = observableHashMap();

    private final ObservableList<Account> accountsData = observableArrayList();
    private final Stage stage;
    private final Workspace preferencesStore;

    private final Map<String, DrilldownTab> indexToTab = new HashMap<>();
    private final DashboardTab dashboard;

    public ISpendPane(final Stage stage, final Workspace preferencesStore) {
        this.stage = stage;
        this.preferencesStore = preferencesStore;

        BorderPane pane = new BorderPane();
        final TabPane tabPane = new TabPane();
        pane.setCenter(tabPane);

        dashboard = new DashboardTab(this, flagLists);
        tabPane.getTabs().add(dashboard);
        addObserver(dashboard.getBudgetView());
        addObserver(dashboard.getAccountsView());

        pane.setId("container");
        pane.setTop(createMenuBar());

        scene = new Scene(pane);
        stage.setScene(scene);
        scene.getStylesheets().add("stylesheet.css");
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.show();

        flagLists.addListener((MapChangeListener<String, ObservableList<Record>>) change -> {
            if (change.wasAdded()) {
                DrilldownTab tb = new DrilldownTab(
                        change.getKey(),
                        change.getValueAdded()
                );
                tabPane.getTabs().add(tb);
                indexToTab.put(change.getKey(), tb);
            } else {
                tabPane.getTabs().remove(indexToTab.get(change.getKey()));
            }
        });
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem open = new MenuItem("Open");
        open.setOnAction(new ImportHandler(preferencesStore, this));

        MenuItem close = new MenuItem("Close");
        close.setOnAction(e -> Platform.exit());

        menu.getItems().addAll(open, close);
        menuBar.getMenus().addAll(menu);
        return menuBar;
    }

    public RecordStore getRecordStore() {
        return preferencesStore.getRecordStore();
    }

//    public void fileSelected(final String path) throws IOException {
//        accountsData.setAll(recordStore.getAccounts());
//        dashboard.setEndOfMonthBalance(recordStore.getEndOfMonthBalance());
//        this.setChanged();
//        this.notifyObservers();
//    }

    public File showDialog() {
        final DirectoryChooser chooser = new DirectoryChooser();
        System.out.println("TODO - Change OPEN to IMPORT");
        return chooser.showDialog(stage);
    }

    public void show() {
        accountsData.clear();
        accountsData.addAll(preferencesStore.getAccounts());
        this.setChanged();
        this.notifyObservers();
        stage.show();
    }

    public List<String> getSavedSearchQueries() {
        return preferencesStore.getSavedQueries();
    }

    public List<Account> getAccounts() {
        return accountsData;
    }

    public List<String> getSavedLabels() {
        return preferencesStore.getSavedLabels();
    }

    public void saveBudgetEntries(ObservableList<BudgetEntry> budgets) {
        preferencesStore.saveBudgetEntries(budgets);
    }
}
