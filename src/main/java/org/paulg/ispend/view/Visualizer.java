package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Visualizer extends TabPane {

    Visualizer(ObservableList<PieChart.Data> pieChartNegData,
               ObservableList<PieChart.Data> pieChartPosData) {
        Tab tab = new Tab();
        tab.setText("Total");

        HBox box = new HBox();
        Node posChart = pieChart("Income", pieChartPosData, 1, 3);
        Node negChart = pieChart("Expenses", pieChartNegData, 2, 3);
        box.getChildren().addAll(posChart, negChart);

        tab.setContent(box);
        getTabs().add(tab);
    }

    private Node pieChart(final String title, final ObservableList<PieChart.Data> data, final int row, final int col) {
        final PieChart chart = new PieChart(data);
        GridPane.setConstraints(chart, row, col, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        chart.setTitle(title);
        return chart;
    }
}
