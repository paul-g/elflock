package org.paulg.ispend.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Week;
import org.paulg.ispend.model.RecordStore;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class HistoricalVisualizer extends TabPane implements Observer {

    private final ISpendPane iSpendPane;
    private RecordStore recordStore;
    private PieChart posChart, negChart;
    private TimeSeriesChart lineChart;
    private RegularTimePeriod timePeriod = new Month();
    private String query;
    private String indicator = "Sum";

    HistoricalVisualizer(ISpendPane iSpendPane) {
        this.iSpendPane = iSpendPane;
        getTabs().add(makeHistoricalTab());
        getTabs().add(makeTotalTab());
    }

    void plotHistoricalData(String query) {
        this.query = query;
        TimeSeries ts = indicator.equals("Sum") ?
                recordStore.getTotalByDescription(query, timePeriod) :
                recordStore.getAveragesByDescription(query, timePeriod);
        lineChart.setTimeSeries(ts, query);

        Map<String, Double> spent = recordStore.getSpentPerItem(query);
        negChart.setData(toPieChartData(spent));

        Map<String, Double> income = recordStore.getIncomePerItem(query);
        posChart.setData(toPieChartData(income));
    }

    private ObservableList<PieChart.Data> toPieChartData(Map<String, Double> in) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        pieData.addAll(in.entrySet().stream().map(me -> new PieChart.Data(me.getKey(), me.getValue())).collect(Collectors.toList()));
        return pieData;
    }

    private Tab makeHistoricalTab() {
        Tab tab = new Tab("Historical");
        lineChart = new TimeSeriesChart();
        VBox box = new VBox();
        HBox hbox = new HBox();
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Month",
                        "Week"
                );
        final ComboBox<String> period = new ComboBox(options);
        period.getSelectionModel().select(0);
        period.setOnAction(event -> {
            String value = period.getValue();
            switch (value) {
                case "Week": timePeriod = new Week(); break;
                case "Month": timePeriod = new Month(); break;
                default: break;
            }
            plotHistoricalData(query);
        });
        ObservableList<String> indOptions =
                FXCollections.observableArrayList(
                        "Sum",
                        "Average"
                );

        final ComboBox<String> indicatorBox = new ComboBox(indOptions);
        indicatorBox.getSelectionModel().select(0);
        indicatorBox.setOnAction(event -> {
            this.indicator = indicatorBox.getValue();
            plotHistoricalData(query);
        });

        hbox.getChildren().addAll(
                UiUtils.label("Period:"), period,
                UiUtils.label("Indicator:"), indicatorBox);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        box.getChildren().addAll(hbox, lineChart);
        tab.setContent(box);
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
