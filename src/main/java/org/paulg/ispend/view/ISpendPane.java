package org.paulg.ispend.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.paulg.ispend.controller.OpenHistoryHandler;
import org.paulg.ispend.main.HistoryFileVisitor;
import org.paulg.ispend.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;

import static javafx.collections.FXCollections.observableArrayList;

public class ISpendPane extends Observable {

    final Scene scene;
    private final ObservableList<Record> data = observableArrayList();
    private final ObservableList<AggregatedRecord> groupData = observableArrayList();
    private final ObservableList<PieChart.Data> pieChartPosData = observableArrayList();
    private final ObservableList<PieChart.Data> pieChartNegData = observableArrayList();
    private final ObservableList<Account> accountsData = observableArrayList();
    private final SearchView searchView;
    private final Stage stage;
    private final GroupView groupView;
    private final PreferencesStore preferencesStore;

    private final AccountSummaryView accountsView;
    private final StaticVisualizer staticVisualizer;
    private RecordStore recordStore;
    private Integer totalSpent;
    private Integer totalIncome;

    public ISpendPane(final Stage stage, final PreferencesStore preferencesStore) {
        this.stage = stage;
        this.preferencesStore = preferencesStore;
        this.staticVisualizer = new StaticVisualizer();
        this.groupView = new GroupView(this, groupData, pieChartPosData, pieChartNegData);
        this.searchView = new SearchView(data);
        this.accountsView = new AccountSummaryView(this);
        addObserver(groupView);
        addObserver(accountsView);

        stage.setTitle("ISpend");

        BorderPane pane = new BorderPane();
        pane.setCenter(makeAppContent());

        pane.setTop(createMenuBar());

        scene = new Scene(pane);
        stage.setScene(scene);
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
        pane.getTabs().add(makeDrillDownTab());
        pane.getTabs().add(makeManageTab());
        return pane;
    }

    private Tab makeManageTab() {
        Tab tab = new Tab("Manage");
        tab.setContent(groupView);
        return tab;
    }

    private Tab makeDrillDownTab() {
        Tab tab = new Tab("Drilldown");
        tab.setContent(this.searchView);
        return tab;
    }

    private Tab makeDashboardTab() {
        Tab tab = new Tab("Dashboard");
        HBox box = new HBox();
        box.getChildren().addAll(accountsView, staticVisualizer);
        box.setHgrow(accountsView, Priority.ALWAYS);
        box.setHgrow(staticVisualizer, Priority.ALWAYS);
        tab.setContent(box);
        return tab;
    }

    private void toNegativePieChartData() {
        double total = (totalSpent == null ? 7500 : totalSpent);
        double leftTotal = total;
        pieChartNegData.clear();
        for (AggregatedRecord record : groupData) {
            pieChartNegData.add(new PieChart.Data(record.getDescription(),
                    (Math.abs(record.getNegative()) / total) * 100));
            leftTotal -= record.getNegative();
        }
        pieChartNegData.add(new PieChart.Data("Other", (leftTotal / total) * 100));
    }

    private void toPositivePieChartData() {
        double total = (totalIncome == null ? 7500 : totalIncome);
        double leftTotal = total;
        pieChartPosData.clear();
        for (AggregatedRecord record : groupData) {
            pieChartPosData.add(new PieChart.Data(record.getDescription(), (record.getPositive() / total) * 100));
            leftTotal -= record.getPositive();
        }
        pieChartPosData.add(new PieChart.Data("Other", (leftTotal / total) * 100));
    }


    public void fileSelected(final String path) throws IOException {
        final HistoryFileVisitor fileVisitor = new HistoryFileVisitor();
        recordStore = fileVisitor.getRecordStore();

        Files.walkFileTree(Paths.get(path), fileVisitor);

        searchView.setRecordStore(recordStore);

        totalSpent = (int) recordStore.getTotalSpent();
        totalIncome = (int) recordStore.getTotalIncome();
        data.clear();
        data.addAll(recordStore.getAllRecords());
        accountsData.clear();
        accountsData.addAll(recordStore.getAccounts());
        recordStore.printSummary();


        staticVisualizer.plotWeeklyTotalData(recordStore.getWeeklyBalance());
        staticVisualizer.plotMonthlyTotalData(recordStore.getMonthlyBalance());

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
            groupView.setText(query);
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
        toPositivePieChartData();
        toNegativePieChartData();
        groupView.plotHistoricalData(byDescription);
    }

    public void saveQuery(String text) {
        preferencesStore.saveQuery(text);
    }

    public List<Account> getAccounts() {
        return accountsData;
    }
}
