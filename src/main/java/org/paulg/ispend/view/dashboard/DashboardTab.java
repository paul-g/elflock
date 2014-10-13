package org.paulg.ispend.view.dashboard;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import org.jfree.data.time.TimeSeries;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.view.ISpendPane;
import org.paulg.ispend.view.utils.UiUtils;

public class DashboardTab extends Tab {
    private final StaticVisualizer staticVisualizer;
    private final AccountSummaryView accountsView;
    private final BudgetView budgetView;

        public DashboardTab(ISpendPane ipane, ObservableMap<String, ObservableList<Record>> flagLists) {
            this.staticVisualizer = new StaticVisualizer();
            this.accountsView = new AccountSummaryView(ipane);
            this.budgetView = new BudgetView(ipane, flagLists);

            setText("Dashboard");
            GridPane pane = new GridPane();
            pane.addRow(0, accountsView, staticVisualizer);
            pane.addRow(1, budgetView.getTableWidget(), budgetView.getPlotWidget());
            UiUtils.setColumnPercentWidths(pane, 40, 60);
            pane.setHgap(10);
            pane.setVgap(10);
            pane.setPadding(new Insets(10, 10, 10, 10));
            setContent(pane);
        }

    public BudgetView getBudgetView() {
        return budgetView;
    }

    public AccountSummaryView getAccountsView() {
        return accountsView;
    }

    public void setEndOfMonthBalance(final TimeSeries records) {
        this.staticVisualizer.setEndOfMonthBalance(records);
    }
}
