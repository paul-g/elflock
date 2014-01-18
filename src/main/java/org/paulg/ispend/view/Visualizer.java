package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import org.paulg.ispend.model.Record;

import java.util.List;

class Visualizer extends TabPane {


    private Node posChart;
    private Node negChart;
    private LineChart<Number,Number> lineChart;
    private XYChart.Series series;
    private int count = 0;

    Visualizer(ObservableList<PieChart.Data> pieChartNegData,
               ObservableList<PieChart.Data> pieChartPosData) {
        getTabs().add(makeTotalTab(pieChartNegData, pieChartPosData));
        getTabs().add(makeHistoricalTab());
    }

    void plotHistoricalData(List<Record> records) {
        series.setName("New Name");
        series.getData().add(new XYChart.Data<>(count, count));
        count++;
    }

    private Tab makeHistoricalTab() {
        Tab tab = new Tab();
        tab.setText("Historical");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        //creating the chart
        lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("History");
        //defining a series
        series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data

        lineChart.getData().add(series);

        tab.setContent(lineChart);
        return tab;
    }

    private Tab makeTotalTab(ObservableList<PieChart.Data> pieChartNegData, ObservableList<PieChart.Data> pieChartPosData) {
        posChart = pieChart("Income", pieChartPosData, 1, 3);
        negChart = pieChart("Expenses", pieChartNegData, 2, 3);

        Tab totalTab = new Tab();
        totalTab.setText("Total");
        HBox box = new HBox();
        box.getChildren().addAll(posChart, negChart);
        totalTab.setContent(box);
        return totalTab;
    }

    private Node pieChart(final String title, final ObservableList<PieChart.Data> data, final int row, final int col) {
        final PieChart chart = new PieChart(data);
        chart.setTitle(title);
        return chart;
    }
}
