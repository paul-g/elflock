package org.paulg.ispend.view;

import javafx.scene.control.Label;

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
}
