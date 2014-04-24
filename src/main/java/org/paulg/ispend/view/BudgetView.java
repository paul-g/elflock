package org.paulg.ispend.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.paulg.ispend.model.RecordStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BudgetView extends HBox implements Observer {

    private final ObservableList<
            BudgetEntry> budgets = FXCollections.observableArrayList();
    private final ISpendPane pane;
    private final HistoricalVisualizer plotWidget;
    private final CompleteTableView<BudgetEntry> tv;
    private final VBox tableWidget;
    private RecordStore recordStore;
    private final List<String> queries = new ArrayList<>();

    public BudgetView(ISpendPane pane) {
        this.pane = pane;
        final Label label = UiUtils.section("Budget");

        plotWidget = new HistoricalVisualizer(pane);
        tv = new CompleteTableView<>(BudgetEntry.class);

        tv.setEditable(true);

        tv.setItems(budgets);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BudgetEntry>() {
            @Override
            public void changed(ObservableValue<? extends BudgetEntry> observableValue, BudgetEntry budgetEntry, BudgetEntry budgetEntry2) {
                if (budgetEntry2 != null)
                    setPlotData(budgetEntry2.getGroup());
            }
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
    }

    @Override
    public void update(Observable o, Object arg) {
        this.recordStore = pane.getRecordStore();
        List<String> queries = pane.getSavedSearchQueries();
        for (String s : queries) {
            addQuery(s);
        }
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
        search.setOnAction(event -> {
            setPlotData(text.getText());
        });

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
        addEntry.setHgrow(text, Priority.ALWAYS);
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
    }

    private BudgetEntry getBudget(String query) {
        double weeklyAvg = recordStore.getWeeklyAverageByDescription(query);
        BudgetEntry be = new BudgetEntry(query, weeklyAvg / 7, weeklyAvg, weeklyAvg * 4);
        return be;
    }

    public Node getTableWidget() {
        return tableWidget;
    }

    public Node getPlotWidget() {
        return plotWidget;
    }
}




