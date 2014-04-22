package org.paulg.ispend.view;

import javafx.scene.control.Label;

/**
 * General configuration options for the app
 */
public class UiUtils {
    public static Label section(String text) {
        final Label l = new Label(text);
        l.getStyleClass().add("section");
        return l;
    }

    public static Label subsection(String text) {
        final Label l = new Label(text);
        l.getStyleClass().add("subsection");
        return l;
    }
}
