package org.paulg.ispend.main;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
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

	final List<TableColumn> columns = new ArrayList<TableColumn>();
	private final TableView<Record> table = new TableView<Record>();
	private static ObservableList<Record> data = FXCollections.observableArrayList();
	private static RecordParser parser;

	public static void main(final String[] args) throws IOException {

		if (args.length != 1) {
			System.out.format("Usage: %s %s \n", ISpend.class.getSimpleName(), "<data-dir>");
			System.exit(1);
		}

		final Path p = Paths.get(args[0]);

		parser = new RecordParser(p);
		data.addAll(parser.getRecordsByAccountName("\"'curent\""));
		data.addAll(parser.getRecordsByAccountName("\"'economii\""));
		parser.printSummary();
		// parser.printSummaryByAccount("\"'economii\"");

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
		search.setMaxWidth(100);

		final Button searchButton = new Button("Search");
		searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					final List<Record> unfiltered = parser.getRecordsByAccountName("\"'curent\"");
					unfiltered.addAll(parser.getRecordsByAccountName("\"'economii\""));
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
				} catch (final IOException e1) {
					e1.printStackTrace();
				}

			}
		});

		vbox.getChildren().addAll(label, table, search, searchButton);

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
		final TableColumn column = new TableColumn(name);
		column.setMinWidth(100);
		column.setCellValueFactory(new PropertyValueFactory<Record, String>(name));
		columns.add(column);
	}
}