package org.paulg.ispend.view.widgets;

import javafx.scene.chart.*;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BarTimeSeriesChart extends BarChart<String, Number> {

    private String format;

    private static CategoryAxis makeDateAxis() {
        CategoryAxis na = new CategoryAxis();
        na.setTickLabelRotation(90);
        na.setTickLabelsVisible(true);
        na.setAnimated(false);
        return na;
    }

    private static NumberAxis makeValueAxis() {
        NumberAxis na = new NumberAxis();
        na.setAnimated(false);
        return na;
    }

    public BarTimeSeriesChart(String format) {
        super(makeDateAxis(), makeValueAxis());
        this.format = format;
    }

    public BarTimeSeriesChart(String format, String title) {
        this(format);
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
            series.getData().add(new XYChart.Data<>(d, value));
        }
        getData().setAll(series);
    }
}