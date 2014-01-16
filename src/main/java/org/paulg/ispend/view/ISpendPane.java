package org.paulg.ispend.view;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Callback;

import org.paulg.ispend.controller.OpenHistoryHandler;
import org.paulg.ispend.main.HistoryFileVisitor;
import org.paulg.ispend.model.*;

import static javafx.collections.FXCollections.*;

public class ISpendPane {

    private final ObservableList<Record> data = observableArrayList();
    private final ObservableList<AggregatedRecord> groupData = observableArrayList();
    private final ObservableList<PieChart.Data> pieChartPosData = observableArrayList();
    private final ObservableList<PieChart.Data> pieChartNegData = observableArrayList();
    private final ObservableList<Account> accountsData = observableArrayList();
    private RecordStore recordStore;

    private TextField groupBy;
    private TextField search;
    private Integer totalSpent;
    private Integer totalIncome;
    private final Stage stage;
    private GridPane gridPane;

    private final PreferencesStore preferencesStore;

    public ISpendPane(final Stage stage, final PreferencesStore preferencesStore) {
        this.stage = stage;
        this.preferencesStore = preferencesStore;
        stage.setTitle("ISpend");

        BorderPane pane = new BorderPane();
        pane.setCenter(makeAppContent());

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem item = new MenuItem("Open");
        item.setOnAction(new OpenHistoryHandler(this));
        menu.getItems().addAll(item);
        menuBar.getMenus().addAll(menu);

        pane.setTop(menuBar);

        final Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.show();
    }

    private Pane makeAppContent() {
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setGridLinesVisible(false);

        gridPane.add(accountSummary(), 0, 0, 3, 1);
        gridPane.add(makeSearchPanel(), 0, 1);
        gridPane.add(makeGroupByPanel(), 1, 1);

        Node posChart = pieChart("Income", pieChartPosData, 1, 3);
        Node negChart = pieChart("Expenses", pieChartNegData, 2, 3);

        final TableView<Record> recordView = makeTable(data, Record.class, 0, 2, 1, 2);
        final TableView<AggregatedRecord> aggregatedRecordView = makeTable(groupData, AggregatedRecord.class, 1, 2, 2,
                                                                           1);

        setColumnConstraints(gridPane, 50, 25, 25);
        gridPane.getChildren().addAll(posChart, negChart, recordView, aggregatedRecordView);
        // gridPane.setGridLinesVisible(true);
        return gridPane;
    }

    private void setColumnConstraints(final GridPane gridPane, final Integer... widths) {
        for (int i : widths) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(i);
            gridPane.getColumnConstraints().add(col);
        }
    }

    private Node pieChart(final String title, final ObservableList<PieChart.Data> data, final int row, final int col) {
        final PieChart chart = new PieChart(data);
        GridPane.setConstraints(chart, row, col, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        chart.setTitle(title);
        return chart;
    }

    private ObservableList<Data> toNegativePieChartData() {
        double total = (totalSpent == null ? 7500 : totalSpent);
        double leftTotal = total;
        pieChartNegData.clear();
        for (AggregatedRecord record : groupData) {
            pieChartNegData.add(new PieChart.Data(record.getDescription(),
                                                  (Math.abs(record.getNegative()) / total) * 100));
            leftTotal -= record.getNegative();
        }
        pieChartNegData.add(new PieChart.Data("Other", (leftTotal / total) * 100));
        return pieChartNegData;
    }

    private ObservableList<Data> toPositivePieChartData() {
        double total = (totalIncome == null ? 7500 : totalIncome);
        double leftTotal = total;
        pieChartPosData.clear();
        for (AggregatedRecord record : groupData) {
            pieChartPosData.add(new PieChart.Data(record.getDescription(), (record.getPositive() / total) * 100));
            leftTotal -= record.getPositive();
        }
        pieChartPosData.add(new PieChart.Data("Other", (leftTotal / total) * 100));
        return pieChartPosData;
    }

    private Node makeGroupByPanel() {
        groupBy = new TextField();
        groupBy.setPromptText("Group by");
        groupBy.setDisable(true);
        groupBy.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                    public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        setQuery(groupBy.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        clearQuery();
                    }
                }

            });
        Button save = new Button();
        save.setText("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                    public void handle(final ActionEvent arg0) {
                    preferencesStore.saveQuery(groupBy.getText());
                }
            });
        final HBox box = new HBox();
        box.getChildren().addAll(groupBy, save);
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(groupBy, Priority.ALWAYS);
        return box;
    }

    private <T> TableView<T> makeTable(final ObservableList<T> data, final Class<T> clazz, final int row,
                                       final int col, final int hSpan, final int vSpan) {
        final TableView<T> table = new CompleteTableView<T>(clazz);
        table.setEditable(true);
        table.setItems(data);
        GridPane.setConstraints(table, row, col, hSpan, vSpan, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,
                                Priority.ALWAYS);
        return table;
    }

    private Node makeSearchPanel() {
        search = new TextField();
        search.setPromptText("Search");
        search.setDisable(true);
        search.setPrefWidth(400);
        search.setOnKeyReleased(new EventHandler<KeyEvent>() {

                @Override
                    public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        filterData(search.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        search.clear();
                        filterData(search.getText());
                    }
                }

                private void filterData(final String searchText) {
                    final List<Record> filtered = recordStore.filter(searchText);
                    data.clear();
                    data.addAll(filtered);
                }

            });

        final HBox box = new HBox();
        box.getChildren().addAll(search);
        return box;
    }

    private Node accountSummary() {
        final Label label = new Label("Accounts");
        label.setFont(new Font("Arial", 20));

        ListView<Account> accounts = new ListView<Account>(accountsData);

        accounts.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
                @Override
                    public ListCell<Account> call(final ListView<Account> arg0) {
                    return new AccountCell();
                }
            });

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
        groupBy.setDisable(false);
        search.setDisable(false);
        totalSpent = (int) recordStore.getTotalSpent();
        totalIncome = (int) recordStore.getTotalIncome();
        data.clear();
        data.addAll(recordStore.getAllRecords());
        accountsData.clear();
        accountsData.addAll(recordStore.getAccounts());
        recordStore.printSummary();
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
            groupBy.setText(query);
        }
        stage.show();
    }

    private void clearQuery() {
        groupData.clear();
    }

    private void setQuery(final String query) {
        clearQuery();
        groupData.addAll(recordStore.groupByDescription(query));
        accountsData.clear();
        accountsData.addAll(recordStore.getAccounts());
        toPositivePieChartData();
        toNegativePieChartData();
    }

}

