package org.paulg.ispend.view;


import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.jfree.data.time.TimeSeries;

public class StaticVisualizer extends VBox {

    private final BarTimeSeriesChart monthlyBalance;

    public StaticVisualizer() {
        super();
        monthlyBalance = new BarTimeSeriesChart("Monthly Balance");
        getChildren().addAll(monthlyBalance);
        setAlignment(Pos.CENTER);
        setSpacing(20);
    }

    public void setMonthlyBalance(final TimeSeries records) {
        monthlyBalance.setTimeSeries(records, "Average Monthly Balance (all accounts)");
    }
}
