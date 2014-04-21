package org.paulg.ispend.view;


import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.util.*;

public class StaticVisualizer extends VBox {

    private LineChart<Number, Number> weeklyBalance, monthlyBalance;

    private NumberAxis makeDateAxis(String dateFormat) {
        NumberAxis na = new NumberAxis();
        na.setTickLabelFormatter(new DateConverter(dateFormat));
        na.setForceZeroInRange(false);
        return na;
    }

    StaticVisualizer() {
        NumberAxis mbAxis = makeDateAxis("Y m");
        NumberAxis wbAxis = makeDateAxis("Y w");

        monthlyBalance = new LineChart<>(mbAxis, new NumberAxis());
        monthlyBalance.setTitle("Monthly Balance");

        weeklyBalance = new LineChart<>(wbAxis, new NumberAxis());
        weeklyBalance.setTitle("Weekly Balance");

        getChildren().addAll(monthlyBalance);

        setAlignment(Pos.CENTER);
        setSpacing(20);
    }

    void plotMonthlyTotalData(Map<Date, Double> records) {
        plotData(records, monthlyBalance, "yy MM", "Monthly Balance");
    }

    void plotWeeklyTotalData(Map<Date, Double> records) {
        plotData(records, weeklyBalance, "yy w", "Weekly Balance");
    }

    private void plotData(
            Map<Date, Double> records,
            LineChart<?, ?> chart,
            String format,
            String seriesTitle) {
        List<Date> allDates = new ArrayList<>(records.keySet());
        Collections.sort(allDates);
        XYChart.Series series = new XYChart.Series();
        series.setName(seriesTitle);
        for (Date d : allDates)
            series.getData().add(new XYChart.Data(d.getTime(), records.get(d)));
        chart.getData().add(series);
    }
}
