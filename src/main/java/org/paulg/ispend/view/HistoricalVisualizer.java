package org.paulg.ispend.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import org.jfree.data.time.TimeSeries;
import org.paulg.ispend.model.RecordStore;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class HistoricalVisualizer extends TabPane implements Observer {

    private final ISpendPane iSpendPane;
    private RecordStore recordStore;
    private PieChart posChart, negChart;
    private TimeSeriesChart lineChart;

    HistoricalVisualizer(ISpendPane iSpendPane) {
        this.iSpendPane = iSpendPane;
        getTabs().add(makeHistoricalTab());
        getTabs().add(makeTotalTab());
    }

    void plotHistoricalData(String query) {
        TimeSeries ts = recordStore.getWeeklyAveragesByDescription(query);
        lineChart.setTitle(query);
        lineChart.setTimeSeries(ts);

        Map<String, Double> spent = recordStore.getSpentPerItem(query);
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> me : spent.entrySet()){
            pieData.add(new PieChart.Data(me.getKey(), me.getValue()));
        }
        System.out.print(spent);
        negChart.setData(pieData);
    }

    private Tab makeHistoricalTab() {
        Tab tab = new Tab("Historical");
        lineChart = TimeSeriesChart.build();
        lineChart.setTitle("History");
        tab.setContent(lineChart);
        tab.setContent(lineChart);
        return tab;
    }

    private Tab makeTotalTab() {
        posChart = pieChart("Income",  FXCollections.observableArrayList());
        negChart = pieChart("Expenses", FXCollections.observableArrayList());

        Tab totalTab = new Tab("Total");
        HBox box = new HBox();
        box.getChildren().addAll(posChart, negChart);
        totalTab.setContent(box);
        return totalTab;
    }

    private PieChart pieChart(final String title, final ObservableList<PieChart.Data> data) {
        final PieChart chart = new PieChart(data);
        chart.setTitle(title);
        return chart;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.recordStore = iSpendPane.getRecordStore();
    }
}
