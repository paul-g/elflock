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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

public class ISpend extends Application {

	private static ObservableList<Record> data = FXCollections.observableArrayList();
	private static ObservableList<AggregatedRecord> groupData = FXCollections.observableArrayList();
	private static RecordParser parser;

	public static void main(final String[] args) throws IOException {
		launch(args);
	}

	private TextField groupBy;
	private TextField search;

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
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.autosize();
		gridPane.setGridLinesVisible(false);

		gridPane.add(makeLabel(), 0, 0);
		gridPane.add(makeSearchPanel(), 1, 1);
		gridPane.add(makeBrowsePanel(stage), 0, 1);
		gridPane.add(makeGroupByPanel(), 2, 1);
		gridPane.add(makeTable(data, Record.class), 0, 2, 2, 1);
		gridPane.add(makeTable(groupData, AggregatedRecord.class), 2, 2);

		return gridPane;
	}

	private Node makeGroupByPanel() {
		groupBy = new TextField();
		groupBy.setPromptText("Group by");
		groupBy.setMinWidth(300);
		groupBy.setDisable(true);

		groupBy.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(final KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					groupData.clear();
					groupData.addAll(parser.groupByDescription(parseArguments(groupBy.getText())));
				} else if (t.getCode() == KeyCode.ESCAPE) {
					groupData.clear();
				}
			}

			private String[] parseArguments(final String text) {
				return text.split(",");
			}

		});

		final HBox box = new HBox();
		box.getChildren().addAll(groupBy);
		return box;
	}

	@SuppressWarnings("unchecked")
	private <T> Node makeTable(final ObservableList<T> data, final Class<T> clazz) {
		final TableView<T> table = new TableView<T>();
		final List<TableColumn<T, String>> columns = makeColumns(clazz);
		table.setEditable(true);
		table.setItems(data);
		table.getColumns().addAll(columns.toArray(new TableColumn[0]));
		return table;
	}

	private Node makeBrowsePanel(final Stage stage) {
		final TextField browse = new TextField();
		browse.setPromptText("Path to data directory");
		browse.setMinWidth(400);
		final HBox box = new HBox();
		box.setSpacing(10);

		final Button browseButton = new Button("Browse");
		browseButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(final ActionEvent e) {
				final DirectoryChooser chooser = new DirectoryChooser();
				final File selectedDirectory = chooser.showDialog(stage);
				final String path = selectedDirectory.getAbsolutePath();
				browse.setText(path);
				try {
					parser = new RecordParser(Paths.get(path));
					search.setDisable(false);
					groupBy.setDisable(false);
				} catch (final IOException e1) {
					// XXX print some nice error
					e1.printStackTrace();
				}
				data.addAll(parser.getAllRecords());
				parser.printSummary();
			}
		});

		box.getChildren().addAll(browse, browseButton);
		return box;
	}

	private Node makeSearchPanel() {
		search = new TextField();
		search.setPromptText("Search");
		search.setMinWidth(300);
		search.setDisable(true);
		search.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(final KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					filterData(search.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					search.clear();
					filterData(search.getText());
				}
			}

			private void filterData(final String searchText) {
				final List<Record> filtered = parser.filter(searchText);
				data.clear();
				data.addAll(filtered);
			}

		});

		final HBox box = new HBox();
		box.getChildren().addAll(search);
		return box;
	}

	private Node makeLabel() {
		final Label label = new Label("Account Balance");
		label.setFont(new Font("Arial", 20));
		return label;
	}

	private <T> List<TableColumn<T, String>> makeColumns(final Class<T> clazz) {
		final List<TableColumn<T, String>> columns = new ArrayList<TableColumn<T, String>>();
		for (final Field f : clazz.getDeclaredFields()) {
			final TableColumn<T, String> column = new TableColumn<T, String>(f.getName());
			column.setCellValueFactory(new PropertyValueFactory<T, String>(f.getName()));
			column.setMinWidth(150);
			columns.add(column);
		}
		return columns;
	}
}