package org.paulg.ispend.controller;

import java.io.*;

import javafx.event.*;

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
				iSpendPane.fileSelected(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}