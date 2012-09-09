package org.paulg.ispend.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

import org.paulg.ispend.view.ISpendPane;

public class ISpend extends Application {

	public static void main(final String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(final Stage stage) {
		ISpendPane pane = new ISpendPane(stage);
		pane.show();
	}

}