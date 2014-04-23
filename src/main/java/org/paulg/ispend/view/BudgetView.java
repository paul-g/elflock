package org.paulg.ispend.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jfree.data.time.TimeSeries;
import org.paulg.ispend.model.RecordStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BudgetView extends HBox implements Observer {

    private final ObservableList<
            BudgetEntry> budgets = FXCollections.observableArrayList();
    private final ISpendPane pane;
    private final TimeSeriesChart plot;
    private RecordStore recordStore;
    private static final List<String> queries = new ArrayList<>();

    public BudgetView(ISpendPane pane) {
        this.pane = pane;
        final Label label = UiUtils.section("Budget");

        plot = TimeSeriesChart.build();

        CompleteTableView<BudgetEntry> tv = new CompleteTableView<>(BudgetEntry.class);

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

        VBox table = new VBox();
        HBox addEntry = addEntry();

        setPadding(new Insets(30, 5, 5, 5));
        setSpacing(5);
        table.getChildren().addAll(addEntry, tv);
        table.setSpacing(5);
        setHgrow(table, Priority.ALWAYS);
        setHgrow(plot, Priority.ALWAYS);
        getChildren().addAll(table, plot);
    }

    private void setPlotData(String group) {
        XYChart.Series series = new XYChart.Series<>();
        TimeSeries tsData = recordStore.getWeeklyAveragesByDescription(group);
        plot.setTimeSeries(tsData);
        series.setName(group + " (Weekly)");
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Update received");
        this.recordStore = pane.getRecordStore();
        List<String> queries = pane.getSavedSearchQueries();
        System.out.println("Adding queries");
        for (String s : queries) {
            addQuery(s);
        }
    }

    private HBox addEntry() {
        HBox addEntry = new HBox();
        final TextField text = new TextField();
        text.setPromptText("Search");
        final Button add = new Button("+");
        addEntry.getChildren().addAll(text, add);
        addEntry.setHgrow(text, Priority.ALWAYS);
        addEntry.setSpacing(10);

        add.setOnAction(event -> {
            addQuery(text.getText());

        });

        text.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                addQuery(text.getText());
                text.clear();
            }
        });

        return addEntry;
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
}




