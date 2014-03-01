package org.paulg.ispend.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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
    private final GroupPane groupView;
    private final PreferencesStore preferencesStore;
    private final Visualizer visualizer;
    private RecordStore recordStore;
    private Integer totalSpent;
    private Integer totalIncome;

    public ISpendPane(final Stage stage, final PreferencesStore preferencesStore) {
        this.stage = stage;
        this.preferencesStore = preferencesStore;
        this.visualizer = new Visualizer(pieChartNegData, pieChartPosData);
        this.groupView = new GroupPane(this);
        this.searchView = new SearchView(data);
        addObserver(groupView);

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
        return tab;
    }

    private Tab makeDrillDownTab() {
        Tab tab = new Tab("Drilldown");
        tab.setContent(this.searchView);
        return tab;
    }

    private Tab makeDashboardTab() {
        Tab tab = new Tab("Dashboard");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setGridLinesVisible(false);

        gridPane.add(accountSummary(), 0, 0, 3, 1);
        gridPane.add(this.groupView, 1, 1);

        final TableView<AggregatedRecord> aggregatedRecordView = makeTable(groupData,
                AggregatedRecord.class,
                1, 2, 2, 1);
        GridPane.setConstraints(visualizer, 1, 3, 2, 1,
                HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        setColumnConstraints(gridPane, 50, 25, 25);
        gridPane.getChildren().addAll(visualizer, aggregatedRecordView);
        gridPane.setGridLinesVisible(false);

        tab.setContent(gridPane);
        return tab;
    }

    private void setColumnConstraints(final GridPane gridPane, final Integer... widths) {
        for (int i : widths) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(i);
            gridPane.getColumnConstraints().add(col);
        }
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

    private <T> TableView<T> makeTable(final ObservableList<T> data, final Class<T> clazz, final int row,
                                       final int col, final int hSpan, final int vSpan) {
        final TableView<T> table = new CompleteTableView<>(clazz);
        table.setEditable(true);
        table.setItems(data);
        GridPane.setConstraints(table, row, col, hSpan, vSpan, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,
                Priority.ALWAYS);
        return table;
    }

    private Node accountSummary() {
        final Label label = new Label("Accounts");
        label.setFont(new Font("Arial", 20));

        ListView<Account> accounts = new ListView<>(accountsData);

        accounts.setCellFactory(listViewAccount -> new AccountCell());

        accounts.setPrefHeight(50);

        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(label, accounts);
        box.setPadding(new Insets(10, 10, 10, 10));
        box.setSpacing(10);
        HBox.setHgrow(accounts, Priority.ALWAYS);

        return box;
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

        visualizer.plotWeeklyTotalData(recordStore.getWeeklyBalance());
        visualizer.plotMonthlyTotalData(recordStore.getMonthlyBalance());

        this.setChanged();
        this.groupView.update(this, null);
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
        visualizer.plotHistoricalData(byDescription);
    }

    public void saveQuery(String text) {
        preferencesStore.saveQuery(text);
    }
}
