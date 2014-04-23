package org.paulg.ispend.view;


import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.jfree.data.time.TimeSeries;

public class StaticVisualizer extends VBox {

    private final  transient TimeSeriesChart monthlyBalance;

    public StaticVisualizer() {
        super();
        monthlyBalance = TimeSeriesChart.build();
        monthlyBalance.setTitle("Monthly Balance");
        getChildren().addAll(monthlyBalance);
        setAlignment(Pos.CENTER);
        setSpacing(20);
    }

    public void setMonthlyBalance(final TimeSeries records) {
        monthlyBalance.setTimeSeries(records);
    }
}
