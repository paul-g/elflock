package org.paulg.ispend.main;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import javafx.application.Application;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

import org.paulg.ispend.model.*;
import org.paulg.ispend.view.CompleteTableView;

public class ISpend extends Application {

	private static ObservableList<Record> data = FXCollections.observableArrayList();
	private static ObservableList<AggregatedRecord> groupData = FXCollections.observableArrayList();
	private static RecordStore parser;

	private TextField groupBy;
	private TextField search;

	public static void main(final String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(final Stage stage) {
		stage.setTitle("ISpend");
		final Scene scene = new Scene(makeAppContent(stage));
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(true);
		stage.show();
	}

	private Pane makeAppContent(final Stage stage) {
		final GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setGridLinesVisible(false);

		gridPane.add(makeLabel(), 0, 0);
		gridPane.add(makeSearchPanel(), 0, 2);
		gridPane.add(makeBrowsePanel(stage), 0, 1);
		gridPane.add(makeGroupByPanel(), 1, 2);

		final TableView<Record> recordView = makeTable(data, Record.class);
		gridPane.add(recordView, 0, 3);
		GridPane.setConstraints(recordView, 0, 3, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		final TableView<AggregatedRecord> aggregatedRecordView = makeTable(groupData, AggregatedRecord.class);
		gridPane.add(aggregatedRecordView, 1, 3);

		final ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(60);
		final ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(40);
		gridPane.getColumnConstraints().addAll(column1, column2);

		return gridPane;
	}

	private Node makeGroupByPanel() {
		groupBy = new TextField();
		groupBy.setPromptText("Group by");
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

	private <T> TableView<T> makeTable(final ObservableList<T> data, final Class<T> clazz) {
		final TableView<T> table = new CompleteTableView<T>(clazz);
		table.setEditable(true);
		table.setItems(data);
		return table;
	}

	private Node makeBrowsePanel(final Stage stage) {
		final TextField browse = new TextField();
		browse.setPromptText("Path to data directory");
		browse.setPrefWidth(400);

		final Button browseButton = new Button("Browse");
		browseButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(final ActionEvent e) {
				final DirectoryChooser chooser = new DirectoryChooser();
				final File selectedDirectory = chooser.showDialog(stage);
				if (selectedDirectory != null) {
					final String path = selectedDirectory.getAbsolutePath();
					browse.setText(path);
					try {
						final HistoryFileVisitor fileVisitor = new HistoryFileVisitor();
						Files.walkFileTree(Paths.get(path), fileVisitor);
						parser = fileVisitor.getRecordStore();
						groupBy.setDisable(false);
						search.setDisable(false);
					} catch (final IOException e1) {
						// XXX print some nice error
						e1.printStackTrace();
					}
					data.addAll(parser.getAllRecords());
					parser.printSummary();
				}
			}
		});

		final HBox box = new HBox();
		box.setSpacing(10);
		box.getChildren().addAll(browse, browseButton);
		return box;
	}

	private Node makeSearchPanel() {
		search = new TextField();
		search.setPromptText("Search");
		search.setDisable(true);
		search.setPrefWidth(400);
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

}