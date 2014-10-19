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
        queryCol.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<BudgetEntry, String>>) event ->
                event.getRowValue().setGroup(event.getNewValue()));

        TableColumn budgetCol = tv.getColumns().get(1);
        budgetCol.setCellFactory(TextFieldTableCell.forTableColumn());
        budgetCol.setOnEditCommit( (EventHandler<TableColumn.CellEditEvent<BudgetEntry, String>>)event -> {
            String oldValue = event.getOldValue();
            String newValue = event.getNewValue();
            event.getRowValue().setLabel(newValue);
            updateFlagList(oldValue, newValue);
        });

        tv.setEditable(true);
        tv.setItems(budgets);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.getSelectionModel().selectedItemProperty().addListener((ov, oldv, newv) -> {
            if (newv != null)
                setPlotData(newv.getGroup(), newv.getLabel());
        });

        this.tableWidget = new VBox();
        tableWidget.getStyleClass().add("vbox");
        HBox addEntry = addEntry();
        tableWidget.getChildren().addAll(addEntry, tv);
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
        List<String> labels = pane.getSavedLabels();
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
            removeQuery(budgets.get(i));

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

    private void removeQuery(BudgetEntry query) {
        List<Record> rs = filterAny(recordStore.getAllRecords(), query.getGroup());
        flagLists.remove(query.getLabel());
        unflagged.addAll(rs);
        budgets.remove(query);
        pane.saveBudgetEntries(budgets);
    }

    private void addQuery(String query, String label) {
        budgets.add(getBudget(query, label));
        pane.saveBudgetEntries(budgets);
        List<Record> rs = filterAny(recordStore.getAllRecords(), query);
        flagLists.put(label, observableArrayList(rs));
        unflagged.removeAll(rs);
    }

    private void updateFlagList(String oldValue, String newValue) {
        flagLists.put(newValue, flagLists.get(oldValue));
        flagLists.remove(oldValue);
        pane.saveBudgetEntries(budgets);
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




