package org.paulg.ispend.main;

import java.io.*;
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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

public class ISpend extends Application {

	final List<TableColumn<Record, String>> columns = new ArrayList<TableColumn<Record, String>>();
	private final TableView<Record> table = new TableView<Record>();
	private static ObservableList<Record> data = FXCollections.observableArrayList();
	private static RecordParser parser;

	public static void main(final String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(final Stage stage) {
		final Scene scene = new Scene(new Group());
		stage.setTitle("ISpend");

		final ToolBar toolBar = new ToolBar();
		final BorderPane borderPane = new BorderPane();
		borderPane.setTop(toolBar);
		borderPane.setCenter(makeAppContent(stage));

		((Group) scene.getRoot()).getChildren().addAll(borderPane);

		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.show();
	}

	private GridPane makeAppContent(final Stage stage) {
		final GridPane gridPane = new GridPane();
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(10, 0, 0, 10));
		gridPane.autosize();
		gridPane.setGridLinesVisible(false);

		makeLabel(gridPane);

		for (final Field f : Record.class.getDeclaredFields()) {
			makeColumns(f.getName());
		}

		makeTable(gridPane);
		makeSearchPanel(gridPane);

		final TextField browse = new TextField();
		browse.setPromptText("Path to data directory");
		browse.setMinWidth(400);
		gridPane.add(browse, 0, 1);

		final Button browseButton = new Button("Browse");
		browseButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(final ActionEvent e) {
				final DirectoryChooser chooser = new DirectoryChooser();
				final File selectedDirectory = chooser.showDialog(stage);
				browse.setText(selectedDirectory.getAbsolutePath());
			}
		});
		gridPane.add(browseButton, 1, 1);

		final Button goButton = new Button("Go");
		goButton.setOnAction(new EventHandler<ActionEvent>() {
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
		gridPane.add(goButton, 2, 1);
		return gridPane;
	}

	private void makeTable(final GridPane gridPane) {
		table.setEditable(true);
		table.setItems(data);
		table.getColumns().addAll(columns.toArray(new TableColumn[0]));
		gridPane.add(table, 0, 2, 3, 1);
	}

	private void makeSearchPanel(final GridPane gridPane) {
		final TextField search = new TextField();
		search.setPromptText("Search");
		search.setMinWidth(300);
		gridPane.add(search, 0, 3);

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
		gridPane.add(searchButton, 1, 3);
	}

	private void makeLabel(final GridPane gridPane) {
		final Label label = new Label("Account Balance");
		label.setFont(new Font("Arial", 20));
		gridPane.add(label, 0, 0);
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