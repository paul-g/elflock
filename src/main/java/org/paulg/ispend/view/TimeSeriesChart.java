package org.paulg.ispend.view;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeSeriesChart extends LineChart<Number, Number>  {

    private TimeSeries ts;

    private static NumberAxis makeDateAxis(String dateFormat) {
        NumberAxis na = new NumberAxis();
        na.setTickLabelFormatter(new DateConverter(dateFormat));
        na.setForceZeroInRange(false);
        return na;
    }

    public static TimeSeriesChart build() {
        return new TimeSeriesChart(makeDateAxis("Y m"), new NumberAxis());
    }

    public TimeSeriesChart(NumberAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
    }

    public void setTimeSeries(TimeSeries ts) {
        this.ts = ts;
        List<Date> allDates = new ArrayList<>();
        XYChart.Series series = new XYChart.Series();
        //series.setName(seriesTitle);

        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem tsItem = ts.getDataItem(i);
            Date date = tsItem.getPeriod().getStart();
            Number value = tsItem.getValue();
            series.getData().add(new XYChart.Data(date.getTime(), value));
        }
        getData().setAll(series);
    }
}
