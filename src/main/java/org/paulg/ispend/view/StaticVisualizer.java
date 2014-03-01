package org.paulg.ispend.view;


import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.*;

public class StaticVisualizer extends VBox {

    private LineChart<String, Number> balanceChart, monthlyBalance;

    StaticVisualizer() {
        monthlyBalance = new LineChart<>(new CategoryAxis(), new NumberAxis());
        monthlyBalance.setTitle("Monthly Balance");

        balanceChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        balanceChart.setTitle("Balance");

        getChildren().addAll(balanceChart, monthlyBalance);
    }

    void plotMonthlyTotalData(Map<Date, Double> records) {
        plotData(records, monthlyBalance, "yy MM", "Monthly Balance");
    }

    void plotWeeklyTotalData(Map<Date, Double> records) {
        plotData(records, balanceChart, "yy w", "Weekly Balance");
    }

    private void plotData(
            Map<Date, Double> records,
            LineChart<?, ?> chart,
            String format,
            String seriesTitle) {
        List<Date> allDates = new ArrayList<>(records.keySet());
        Collections.sort(allDates);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        XYChart.Series series = new XYChart.Series();
        series.setName(seriesTitle);
        for (Date d : allDates)
            series.getData().add(new XYChart.Data(sdf.format(d), records.get(d)));
        chart.getData().add(series);
    }
}
