package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import org.jfree.data.time.TimeSeries;
import org.paulg.ispend.model.RecordStore;

import java.util.Observable;
import java.util.Observer;

public class HistoricalVisualizer extends TabPane implements Observer {

    private final ISpendPane iSpendPane;
    private RecordStore recordStore;
    private Node posChart, negChart;
    private TimeSeriesChart lineChart;

    HistoricalVisualizer(
            ISpendPane iSpendPane,
            ObservableList<PieChart.Data> pieChartNegData,
            ObservableList<PieChart.Data> pieChartPosData) {
        this.iSpendPane = iSpendPane;
        getTabs().add(makeTotalTab(pieChartNegData, pieChartPosData));
        getTabs().add(makeHistoricalTab());
    }

    void plotHistoricalData(String query) {

        TimeSeries ts = recordStore.getWeeklyAveragesByDescription(query);
        lineChart.setTitle(query);
        lineChart.setTimeSeries(ts);
    }

    private Tab makeHistoricalTab() {
        Tab tab = new Tab("Historical (Line)");
        lineChart = TimeSeriesChart.build();
        lineChart.setTitle("History");
        tab.setContent(lineChart);
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

    @Override
    public void update(Observable o, Object arg) {
        this.recordStore = iSpendPane.getRecordStore();
    }
}
