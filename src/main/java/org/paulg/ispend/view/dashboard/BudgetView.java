package org.paulg.ispend.view.dashboard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.model.RecordStore;
import org.paulg.ispend.view.widgets.CompleteTableView;
import org.paulg.ispend.view.ISpendPane;

import java.util.*;
import java.util.function.Function;

import static javafx.collections.FXCollections.observableArrayList;

public class BudgetView extends HBox implements Observer {

    private final ObservableList<BudgetEntry> budgets = observableArrayList();
    private final ISpendPane pane;
    private final HistoricalVisualizer plotWidget;
    private final CompleteTableView<BudgetEntry> tv;
    private final VBox tableWidget;
    private final ObservableMap<String, ObservableList<Record>> flagLists;
    private ObservableList<Record> unflagged;
    private RecordStore recordStore;
    private final List<String> queries = new ArrayList<>();

    private ObservableValue getEntries(Function<BudgetEntry, Double> func, TableColumn.CellDataFeatures cd) {
        return new SimpleStringProperty(String.format("%.2f", func.apply((BudgetEntry)cd.getValue())));
    }

    public BudgetView(ISpendPane pane, ObservableMap<String, ObservableList<Record>> flagLists) {
        this.pane = pane;
        this.flagLists = flagLists;
        plotWidget = new HistoricalVisualizer(pane);
        tv = new CompleteTableView<>(BudgetEntry.class);
        ObservableList<TableColumn<BudgetEntry, ?>> tc = tv.getColumns();
        tc.get(1).setCellValueFactory(cd -> getEntries(BudgetEntry::getWeekly, cd));
        tc.get(2).setCellValueFactory(cd -> getEntries(BudgetEntry::getMonthly, cd));
        tc.get(3).setCellValueFactory(cd -> getEntries(BudgetEntry::getDaily, cd));

        tv.setItems(budgets);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.getSelectionModel().selectedItemProperty().addListener((ov, oldv, newv) -> {
            if (newv != null)
                setPlotData(newv.getGroup());
        });

        this.tableWidget = new VBox();
        HBox addEntry = addEntry();
        setPadding(new Insets(30, 5, 5, 5));
        setSpacing(5);
        tableWidget.getChildren().addAll(addEntry, tv);
        tableWidget.setSpacing(5);
        setHgrow(tableWidget, Priority.ALWAYS);
        setHgrow(plotWidget, Priority.ALWAYS);
        getChildren().addAll(tableWidget, plotWidget);


    }

    private void setPlotData(String group) {
        plotWidget.plotHistoricalData(group);
        getBudget(group);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.recordStore = pane.getRecordStore();
        List<String> queries = pane.getSavedSearchQueries();
        unflagged = observableArrayList(recordStore.getAllRecords());
        flagLists.put(
                "Unflagged",
                unflagged
        );
        queries.forEach(this::addQuery);
        this.plotWidget.update(o, arg);
    }

    private HBox addEntry() {
        HBox addEntry = new HBox();
        final TextField text = new TextField();
        text.setPromptText("Search");
        final Button add = new Button("+");

        add.setOnAction(event -> {
            addQuery(text.getText());
            text.clear();
        });

        text.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                setPlotData(text.getText());
            }
        });

        final Button search = new Button("S");
        search.setOnAction(event -> setPlotData(text.getText()));

        final Button delete = new Button("-");
        delete.setOnAction(event -> {
            int i = tv.getSelectionModel().getFocusedIndex();
            int n = tv.getItems().size() - 1;
            if (i < 0)
                return;
            removeQuery(i);

            if (n > 0) {
                int selNext = ((i - 1) % n + n) % n;
                tv.getSelectionModel().select(selNext);
            }
        });

        addEntry.getChildren().addAll(text, search, add, delete);
        HBox.setHgrow(text, Priority.ALWAYS);
        addEntry.setSpacing(10);

        return addEntry;
    }

    private void removeQuery(int i) {
        queries.remove(i);
        budgets.remove(i);
        pane.saveSearchQueries(queries);
    }

    private void addQuery(String query) {
        queries.add(query);
        budgets.add(getBudget(query));
        pane.saveSearchQueries(queries);

        List<Record> rs = RecordStore.filterAny(recordStore.getAllRecords(), query);
        flagLists.put(query, observableArrayList(rs));

        unflagged.removeAll(rs);
    }

    private BudgetEntry getBudget(String query) {
        double weeklyAvg = recordStore.getWeeklyAverageByDescription(query);
        return new BudgetEntry(query, weeklyAvg / 7, weeklyAvg, weeklyAvg * 4);
    }

    public Node getTableWidget() {
        return tableWidget;
    }

    public Node getPlotWidget() {
        return plotWidget;
    }
}




