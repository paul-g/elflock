package org.paulg.ispend.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.paulg.ispend.model.RecordStore;

import java.util.Observable;
import java.util.Observer;

public class BudgetView extends VBox implements Observer {

    private final ObservableList<
            BudgetEntry> budgets = FXCollections.observableArrayList();
    private final ISpendPane pane;
    private RecordStore recordStore;

    public BudgetView(ISpendPane pane) {
        this.pane = pane;
        final Label label = AppConfig.section("Budget");

        CompleteTableView<BudgetEntry> tv = new CompleteTableView<>(BudgetEntry.class);

        tv.setEditable(true);

        tv.setItems(budgets);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox addEntry = addEntry();

        setPadding(new Insets(30, 5, 5, 5));
        setSpacing(5);

        getChildren().addAll(label, addEntry, tv);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Update received");
        this.recordStore = pane.getRecordStore();
        budgets.add(getBudget("tesco"));
    }

    private HBox addEntry() {
        HBox addEntry = new HBox();
        final TextField text = new TextField();
        final Button add = new Button("Add");
        addEntry.getChildren().addAll(text, add);
        addEntry.setHgrow(text, Priority.ALWAYS);
        addEntry.setSpacing(10);

        add.setOnAction(event -> {
            budgets.add(getBudget(text.getText()));
        });

        return addEntry;
    }

    private BudgetEntry getBudget(String query) {
        double weeklyAvg = recordStore.getWeeklyAverageByDescription(query);
        BudgetEntry be = new BudgetEntry(query, weeklyAvg / 7, weeklyAvg, weeklyAvg * 4);
        return be;
    }
}




