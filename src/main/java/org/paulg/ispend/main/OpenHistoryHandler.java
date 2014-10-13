package org.paulg.ispend.main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.paulg.ispend.view.ISpendPane;

import java.io.File;
import java.io.IOException;

public class OpenHistoryHandler implements EventHandler<ActionEvent> {

    private final ISpendPane iSpendPane;

    public OpenHistoryHandler(final ISpendPane iSpendPane) {
        this.iSpendPane = iSpendPane;
    }

    @Override
    public void handle(final ActionEvent e) {
        handleSelection();
    }

    private void handleSelection() {
        final File selectedDirectory = iSpendPane.showDialog();
        if (selectedDirectory != null) {
            final String path = selectedDirectory.getAbsolutePath();
            try {
                iSpendPane.fileSelected(path);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}