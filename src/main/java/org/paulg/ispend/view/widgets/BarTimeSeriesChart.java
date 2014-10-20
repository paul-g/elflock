package org.paulg.ispend.view.widgets;

import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        setHorizontalGridLinesVisible(false);
        setVerticalGridLinesVisible(false);
        this.format = format;
    }

    public BarTimeSeriesChart(String format, String title) {
        this(format);
        setTitle(title);
    }

    public void setTimeSeries(TimeSeries ts, String title) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem tsItem = ts.getDataItem(i);
            Date date = tsItem.getPeriod().getStart();
            Number value = tsItem.getValue();
            String d = sdf.format(date);
            Data<String, Number> data = new XYChart.Data<>(d, value);
            data.setNode(new HoverLabel(value.doubleValue()));
            series.getData().add(data);
        }
        getData().setAll(series);
    }

    class HoverLabel extends StackPane {
        HoverLabel(double value) {

            final Label label = new Label(String.format("%.2f", value));
            label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

            setOnMouseEntered(mouseEvent -> {
                getChildren().setAll(label);
                toFront();
            });

            setOnMouseExited(mouseEvent -> {
                getChildren().clear();
            });
        }

    }
}