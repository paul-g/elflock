package org.paulg.ispend.workspace;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.paulg.ispend.view.ISpendPane;

import java.io.File;

public class ImportHandler implements EventHandler<ActionEvent> {

    private final Workspace workspace;
    private final ISpendPane pane;

    public ImportHandler(final Workspace workspace,
                         final ISpendPane pane) {
        this.workspace = workspace;
        this.pane = pane;
    }

    @Override
    public void handle(final ActionEvent e) {
        final File selectedDirectory = pane.showDialog();
        if (selectedDirectory != null)
            workspace.importFiles(selectedDirectory.toPath());
    }

}