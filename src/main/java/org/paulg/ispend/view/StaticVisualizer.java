package org.paulg.ispend.view;


import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.jfree.data.time.TimeSeries;

public class StaticVisualizer extends VBox {

    private TimeSeriesChart weeklyBalance, monthlyBalance;

    StaticVisualizer() {

        monthlyBalance = TimeSeriesChart.build();
        monthlyBalance.setTitle("Monthly Balance");

        weeklyBalance = TimeSeriesChart.build();
        weeklyBalance.setTitle("Weekly Balance");

        getChildren().addAll(monthlyBalance);

        setAlignment(Pos.CENTER);
        setSpacing(20);
    }

    void plotMonthlyTotalData(TimeSeries records) {
        plotData(records, monthlyBalance);
    }

    void plotWeeklyTotalData(TimeSeries records) {
        plotData(records, weeklyBalance);
    }

    private void plotData(
            TimeSeries records,
            TimeSeriesChart chart) {
        chart.setTimeSeries(records);
    }
}
