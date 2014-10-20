package org.paulg.ispend.view.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jfree.data.time.TimeSeries;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.view.ISpendPane;
import org.paulg.ispend.view.utils.UiUtils;

public class DashboardTab extends Tab {
    private final StaticVisualizer staticVisualizer;
    private final AccountSummaryView accountsView;
    private final BudgetView budgetView;
    private final ISpendPane ipane;

    public DashboardTab(ISpendPane ipane, ObservableMap<String, ObservableList<Record>> flagLists) {
            this.ipane = ipane;
            this.staticVisualizer = new StaticVisualizer();
            this.accountsView = new AccountSummaryView(ipane);
            this.budgetView = new BudgetView(ipane, flagLists);
            setText("Dashboard");
            GridPane pane = new GridPane();
            pane.getStyleClass().add("dashboard-pane");
            pane.addRow(0, accountsView, makeStvBox());
            pane.addRow(1, budgetView.getTableWidget(), budgetView.getPlotWidget());
            UiUtils.setColumnPercentWidths(pane, 40, 60);
            setContent(pane);
        }

    private VBox makeStvBox() {
        VBox stvBox = new VBox();
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "End of Month",
                        "Spent per Month"
                );
        final ComboBox<String> period = new ComboBox<>(options);
        period.getSelectionModel().select(0);
        period.setOnAction(event -> {
            String value = period.getValue();
            switch (value) {
                case "End of Month":
                    staticVisualizer.setSeries(
                            ipane.getRecordStore().getEndOfMonthBalance(),
                            "End of Month Balance (all accounts)");
                    break;
                case "Spent per Month":
                    staticVisualizer.setSeries(
                            ipane.getRecordStore().getSpentPerMonth(),
                            "Spent per Month (all accounts)");
                    break;
                default: break;
            }
        });
        HBox hbox = new HBox();
        hbox.getChildren().addAll(UiUtils.label("Indicator: "), period);
        stvBox.getChildren().addAll(hbox, this.staticVisualizer);
        return stvBox;
    }

    public BudgetView getBudgetView() {
        return budgetView;
    }

    public AccountSummaryView getAccountsView() {
        return accountsView;
    }

    public void setEndOfMonthBalance(final TimeSeries records) {
        this.staticVisualizer.setSeries(records, "End of Month Balance (all accounts)");
    }
}
