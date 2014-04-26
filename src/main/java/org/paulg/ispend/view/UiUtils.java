package org.paulg.ispend.view;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

/**
 * General configuration options for the app
 */
public final class UiUtils {

    private UiUtils() {}

    public static Label section(final String text) {
        final Label label = new Label(text);
        label.getStyleClass().add("section");
        return label;
    }

    public static Label subsection(final String text) {
        final Label label = new Label(text);
        label.getStyleClass().add("subsection");
        return label;
    }

    public static Label label(final String text) {
        final Label label = new Label(text);
        label.getStyleClass().add("label");
        return label;
    }

    public static void setColumnPercentWidths(GridPane pane, double... widths) {
        List<ColumnConstraints> colConstraints = new ArrayList<>();
        for (double w : widths) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(w);
            pane.getColumnConstraints().add(c);
        }
    }
}
