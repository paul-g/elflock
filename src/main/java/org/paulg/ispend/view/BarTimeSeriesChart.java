package org.paulg.ispend.view;

import javafx.scene.chart.*;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BarTimeSeriesChart extends BarChart<String, Number> {

    private static final String format = "Y M W";

    private static CategoryAxis makeDateAxis(String dateFormat) {
        CategoryAxis na = new CategoryAxis();
        return na;
    }

    public BarTimeSeriesChart() {
        super(makeDateAxis(format), new NumberAxis());
    }

    public BarTimeSeriesChart(String title) {
        super(makeDateAxis(format), new NumberAxis());
        setTitle(title);
    }

    public void setTimeSeries(TimeSeries ts, String title) {
        XYChart.Series series = new XYChart.Series();
        series.setName(title);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem tsItem = ts.getDataItem(i);
            Date date = tsItem.getPeriod().getStart();
            Number value = tsItem.getValue();
            String d = sdf.format(date);
            series.getData().add(new XYChart.Data<String, Number>(d, value));
        }
        getData().setAll(series);
    }
}