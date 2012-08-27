package org.paulg.ispend.main;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.*;

import javafx.application.Application;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ISpend extends Application {

	final List<TableColumn<Record, String>> columns = new ArrayList<TableColumn<Record, String>>();
	private final TableView<Record> table = new TableView<Record>();
	private static ObservableList<Record> data = FXCollections.observableArrayList();
	private static RecordParser parser;

	public static void main(final String[] args) throws IOException {

		/*
		 * if (args.length != 1) { System.out.format("Usage: %s %s \n", ISpend.class.getSimpleName(), "<data-dir>");
		 * System.exit(1); }
		 */

		launch(args);
	}

	@Override
	public void start(final Stage stage) {
		final Scene scene = new Scene(new Group());
		stage.setTitle("Table View Sample");
		stage.setWidth(1400);
		stage.setHeight(1000);

		final Label label = new Label("Account Balance");
		label.setFont(new Font("Arial", 20));

		table.setEditable(true);

		for (final Field f : Record.class.getDeclaredFields()) {
			makeColumns(f.getName());
		}

		table.setItems(data);
		table.getColumns().addAll(columns.toArray(new TableColumn[0]));

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.autosize();
		vbox.setPadding(new Insets(10, 0, 0, 10));

		final TextField search = new TextField();
		search.setPromptText("Search");
		search.setMaxWidth(200);

		final Button searchButton = new Button("Search");
		searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				final List<Record> unfiltered = parser.getAllRecords();
				final List<Record> filtered = new ArrayList<Record>();
				final String searchText = search.getText();
				for (final Record r : unfiltered) {
					if (r.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
						filtered.add(r);
					}
				}
				data.clear();
				data.addAll(filtered);
				search.clear();

			}
		});

		final TextField browse = new TextField();
		browse.setPromptText("Path to data directory");
		browse.setMaxWidth(400);

		final Button browseButton = new Button("Go");
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				final String path = browse.getText();
				try {
					System.out.println("Path: " + path);
					parser = new RecordParser(Paths.get(path));
					data.addAll(parser.getAllRecords());
					parser.printSummary();
				} catch (final IOException e1) {
					// XXX print some nice error
					e1.printStackTrace();
				}
			}
		});

		vbox.getChildren().addAll(label, browse, browseButton, table, search, searchButton);

		((Group) scene.getRoot()).getChildren().addAll(vbox);

		stage.setScene(scene);
		stage.show();
	}

	private void makeColumns(final String... args) {
		for (final String s : args) {
			makeColumn(s);
		}
	}

	private void makeColumn(final String name) {
		final TableColumn<Record, String> column = new TableColumn<Record, String>(name);
		column.setMinWidth(150);
		column.setCellValueFactory(new PropertyValueFactory<Record, String>(name));
		columns.add(column);
	}
}