package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import org.paulg.ispend.model.AggregatedRecord;
import org.paulg.ispend.model.Record;

import java.text.SimpleDateFormat;
import java.util.*;

class Visualizer extends TabPane {

    private Node posChart, negChart;
    private LineChart<String, Number> lineChart;

    Visualizer(ObservableList<PieChart.Data> pieChartNegData,
               ObservableList<PieChart.Data> pieChartPosData) {
        getTabs().add(makeTotalTab(pieChartNegData, pieChartPosData));
        getTabs().add(makeHistoricalTab());
    }

    void plotHistoricalData(List<AggregatedRecord> records) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM yy");

        LinkedHashSet<String> allMonths = getAllMonthsInRecordRange(records, sdf);

        lineChart.getData().removeAll();
        lineChart.getData().clear();

        // tag -> month -> total
        for (AggregatedRecord ar : records) {
            XYChart.Series series = new XYChart.Series();
            series.setName(ar.getDescription());
            Map<String, Double> monthToTotal = new HashMap<>();
            for (Record r : ar.getRecords()) {
                String date = sdf.format(r.getDate());
                monthToTotal.compute(date,
                        (k, v) -> (v == null) ? r.getValue() : r.getValue() + v);
            }

            for (String s : allMonths) {
                Double total = monthToTotal.get(s);
                series.getData().add(new XYChart.Data(s, total == null ? 0 : total));
            }

            lineChart.getData().add(series);
        }
    }

    private LinkedHashSet<String> getAllMonthsInRecordRange(List<AggregatedRecord> records, SimpleDateFormat sdf) {
        List<Date> dates = new ArrayList<>();
        for (AggregatedRecord aggregatedRecord : records) {
            for (Record r : aggregatedRecord.getRecords()) {
                dates.add(r.getDate());
            }
        }

        Date maxDate = Collections.max(dates);
        Calendar c = Calendar.getInstance();
        c.setTime(Collections.min(dates));

        LinkedHashSet<String> allMonths = new LinkedHashSet<>();
        while (c.getTime().before(maxDate)) {
            allMonths.add(sdf.format(c.getTime()));
            c.add(Calendar.MONTH, 1);
        }

        allMonths.add(sdf.format(maxDate));
        return allMonths;
    }

    private Tab makeHistoricalTab() {
        Tab tab = new Tab("Historical (Line)");
        lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        lineChart.setTitle("History");
        tab.setContent(lineChart);
        return tab;
    }

    private Tab makeTotalTab(ObservableList<PieChart.Data> pieChartNegData, ObservableList<PieChart.Data> pieChartPosData) {
        posChart = pieChart("Income", pieChartPosData);
        negChart = pieChart("Expenses", pieChartNegData);

        Tab totalTab = new Tab("Total");
        HBox box = new HBox();
        box.getChildren().addAll(posChart, negChart);
        totalTab.setContent(box);
        return totalTab;
    }

    private Node pieChart(final String title, final ObservableList<PieChart.Data> data) {
        final PieChart chart = new PieChart(data);
        chart.setTitle(title);
        return chart;
    }
}
