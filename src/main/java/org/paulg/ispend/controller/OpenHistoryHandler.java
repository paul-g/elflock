package org.paulg.ispend.controller;

import java.io.*;
import java.nio.file.*;

import javafx.event.*;

import org.paulg.ispend.main.HistoryFileVisitor;
import org.paulg.ispend.view.ISpendPane;

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
				final HistoryFileVisitor fileVisitor = new HistoryFileVisitor();
				Files.walkFileTree(Paths.get(path), fileVisitor);
				iSpendPane.fileSelected(fileVisitor.getRecordStore());
			} catch (final IOException e1) {
				// XXX print some nice error
				e1.printStackTrace();
			}
		}
	}
}