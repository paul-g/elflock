package org.paulg.ispend.view;


import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.jfree.data.time.TimeSeries;

public class StaticVisualizer extends VBox {

    private final BarTimeSeriesChart monthlyBalance;

    public StaticVisualizer() {
        super();
        monthlyBalance = new BarTimeSeriesChart("Y M", "End of Month Balance");
        getChildren().addAll(monthlyBalance);
        setAlignment(Pos.CENTER);
        setSpacing(20);
    }

    public void setEndOfMonthBalance(final TimeSeries records) {
        monthlyBalance.setTimeSeries(records, "End of Month Balance (all accounts)");
    }
}
