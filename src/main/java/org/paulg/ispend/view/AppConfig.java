package org.paulg.ispend.view;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * General configuration options for the app
 */
public class AppConfig {
    public static Label section(String text) {
        final Label l = new Label(text);
        l.setFont(new Font("Arial", 20));
        return l;
    }
}
