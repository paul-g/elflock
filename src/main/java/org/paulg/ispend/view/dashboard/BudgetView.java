package org.paulg.ispend.view.dashboard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.store.RecordStore;
import org.paulg.ispend.view.widgets.CompleteTableView;
import org.paulg.ispend.view.ISpendPane;

import java.util.*;
import java.util.function.Function;

import static javafx.collections.FXCollections.observableArrayList;
import static org.paulg.ispend.store.Query.filterAny;

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
    private List<String> labels = new ArrayList<>();

    private ObservableValue getEntries(Function<BudgetEntry, Double> func, TableColumn.CellDataFeatures cd) {
        return new SimpleStringProperty(String.format("%.2f", func.apply((BudgetEntry)cd.getValue())));
    }

    public BudgetView(ISpendPane pane, ObservableMap<String, ObservableList<Record>> flagLists) {
        this.pane = pane;
        this.flagLists = flagLists;
        plotWidget = new HistoricalVisualizer(pane);
        tv = new CompleteTableView<>(BudgetEntry.class);
        ObservableList<TableColumn<BudgetEntry, ?>> tc = tv.getColumns();
        tc.get(2).setCellValueFactory(cd -> getEntries(BudgetEntry::getWeekly, cd));

        TableColumn queryCol = tv.getColumns().get(0);
        queryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        queryCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<BudgetEntry, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<BudgetEntry, String> event) {
                String newQuery = event.getNewValue();
                String oldQuery = event.getOldValue();
                event.getRowValue().setGroup(newQuery);
                updateQuery(oldQuery, newQuery, event.getRowValue().getLabel());
            }
        });

        TableColumn budgetCol = tv.getColumns().get(1);
        budgetCol.setCellFactory(TextFieldTableCell.forTableColumn());
        budgetCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<BudgetEntry, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<BudgetEntry, String> event) {
                int i = labels.indexOf(event.getOldValue());
                labels.set(i, event.getNewValue());
                event.getRowValue().setLabel(event.getNewValue());
                String q = event.getRowValue().getGroup();
                updateQuery(q, q, event.getNewValue());
            }
        });

        tv.setEditable(true);
        tv.setItems(budgets);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.getSelectionModel().selectedItemProperty().addListener((ov, oldv, newv) -> {
            if (newv != null)
                setPlotData(newv.getGroup(), newv.getLabel());
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

    private void setPlotData(String group, String label) {
        plotWidget.plotHistoricalData(group);
        getBudget(group, label);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.recordStore = pane.getRecordStore();
        List<String> queries = pane.getSavedSearchQueries();
        labels = pane.getSavedLabels();
        while (labels.size() < queries.size()) {
            labels.add("Label");
        }
        unflagged = observableArrayList(recordStore.getAllRecords());
        flagLists.put(
                "Unflagged",
                unflagged
        );
        for (int i = 0; i < queries.size(); i++) {
            addQuery(queries.get(i), labels.get(i));
        }
        this.plotWidget.update(o, arg);
    }

    private HBox addEntry() {
        HBox addEntry = new HBox();
        final TextField text = new TextField();
        text.setPromptText("Search");
        final Button add = new Button("+");

        add.setOnAction(event -> {
            addQuery(text.getText(), "Add label...");
            text.clear();
        });

        text.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                setPlotData(text.getText(), text.getText());
            }
        });

        final Button search = new Button("S");
        search.setOnAction(event -> setPlotData(text.getText(), text.getText()));

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

    private void updateQuery(String oldValue, String newValue, String label) {
        int i = queries.indexOf(oldValue);
        queries.set(i, newValue);
        removeQuery(oldValue);
        addQuery2(newValue, label);
    }

    private void removeQuery(int i) {
        String query = queries.get(i);
        queries.remove(i);
        budgets.remove(i);
        labels.remove(i);
        removeQuery(query);
    }

    private void removeQuery(String query) {
        pane.saveSearchQueries(queries);
        pane.saveLabels(labels);
        List<Record> rs = filterAny(recordStore.getAllRecords(), query);
        flagLists.remove(query);
        unflagged.addAll(rs);
    }

    private void addQuery(String query, String label) {
        queries.add(query);
        labels.add(label);
        budgets.add(getBudget(query, label));
        addQuery2(query, label);
    }

    private void addQuery2(String query, String label) {
        pane.saveSearchQueries(queries);
        pane.saveLabels(labels);
        List<Record> rs = filterAny(recordStore.getAllRecords(), query);
        flagLists.put(label, observableArrayList(rs));
        unflagged.removeAll(rs);
    }

    private BudgetEntry getBudget(String query, String label) {
        double weeklyAvg = recordStore.getWeeklyAverageByDescription(query);
        return new BudgetEntry(query, label, weeklyAvg);
    }

    public Node getTableWidget() {
        return tableWidget;
    }

    public Node getPlotWidget() {
        return plotWidget;
    }
}




