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

class Visualizer extends TabPane {

    Visualizer(ObservableList<PieChart.Data> pieChartNegData,
               ObservableList<PieChart.Data> pieChartPosData) {
        getTabs().add(makeTotalTab(pieChartNegData, pieChartPosData));
        getTabs().add(makeHistoricalTab());
    }

    private Tab makeHistoricalTab() {
        Tab tab = new Tab();
        tab.setText("Historical");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("History");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data
        series.getData().add(new XYChart.Data(1, 1));
        series.getData().add(new XYChart.Data(2, 2));
        series.getData().add(new XYChart.Data(3, 3));
        series.getData().add(new XYChart.Data(4, 4));
        lineChart.getData().add(series);

        tab.setContent(lineChart);
        return tab;
    }

    private Tab makeTotalTab(ObservableList<PieChart.Data> pieChartNegData, ObservableList<PieChart.Data> pieChartPosData) {
        Node posChart = pieChart("Income", pieChartPosData, 1, 3);
        Node negChart = pieChart("Expenses", pieChartNegData, 2, 3);

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
