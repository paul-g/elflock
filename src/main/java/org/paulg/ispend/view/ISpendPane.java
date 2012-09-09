package org.paulg.ispend.view;

import java.io.File;
import java.util.List;

import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

import org.paulg.ispend.controller.OpenHistoryHandler;
import org.paulg.ispend.model.*;

public class ISpendPane {

	private final ObservableList<Record> data = FXCollections.observableArrayList();
	private final ObservableList<AggregatedRecord> groupData = FXCollections.observableArrayList();
	private ObservableList<PieChart.Data> pieChartPosData = FXCollections.observableArrayList();
	private ObservableList<PieChart.Data> pieChartNegData = FXCollections.observableArrayList();
	private RecordStore recordStore;

	private TextField groupBy;
	private TextField search;
	private Integer totalSpent;
	private Integer totalIncome;
	private final Stage stage;

	public ISpendPane(final Stage stage) {
		this.stage = stage;
		stage.setTitle("ISpend");

		BorderPane pane = new BorderPane();
		pane.setCenter(makeAppContent(stage));

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("File");
		MenuItem item = new MenuItem("Open");
		item.setOnAction(new OpenHistoryHandler(this));
		menu.getItems().addAll(item);
		menuBar.getMenus().addAll(menu);

		pane.setTop(menuBar);

		final Scene scene = new Scene(pane);

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
		gridPane.add(makeSearchPanel(), 0, 1);
		gridPane.add(makeGroupByPanel(), 1, 1);

		Node posChart = makePositivePieChart();
		gridPane.add(posChart, 1, 4);
		GridPane.setConstraints(posChart, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		Node pieChart = makeNegativePieChart();
		gridPane.add(pieChart, 2, 4);
		GridPane.setConstraints(pieChart, 2, 4, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		final TableView<Record> recordView = makeTable(data, Record.class);
		gridPane.add(recordView, 0, 3);
		GridPane.setConstraints(recordView, 0, 3, 1, 3, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		final TableView<AggregatedRecord> aggregatedRecordView = makeTable(groupData, AggregatedRecord.class);
		gridPane.add(aggregatedRecordView, 1, 3);
		GridPane.setConstraints(aggregatedRecordView, 1, 3, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,
				Priority.ALWAYS);

		final ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		final ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(25);
		final ColumnConstraints column3 = new ColumnConstraints();
		column3.setPercentWidth(25);

		gridPane.getColumnConstraints().addAll(column1, column2, column3);

		return gridPane;
	}

	private Node makeNegativePieChart() {
		pieChartNegData = toNegativePieChartData();
		final PieChart chart = new PieChart(pieChartNegData);
		chart.setTitle("Expenses");
		return chart;
	}

	private Node makePositivePieChart() {
		pieChartPosData = toPositivePieChartData();
		final PieChart chart = new PieChart(pieChartPosData);
		chart.setTitle("Income");
		return chart;
	}

	private ObservableList<Data> toNegativePieChartData() {
		double total = (totalSpent == null ? 7500 : totalSpent);
		double leftTotal = total;
		pieChartNegData.clear();
		for (AggregatedRecord record : groupData) {
			pieChartNegData.add(new PieChart.Data(record.getDescription(),
					(Math.abs(record.getNegative()) / total) * 100));
			leftTotal -= record.getNegative();
		}
		pieChartNegData.add(new PieChart.Data("Other", (leftTotal / total) * 100));
		return pieChartNegData;
	}

	private ObservableList<PieChart.Data> toPositivePieChartData() {
		double total = (totalIncome == null ? 7500 : totalIncome);
		double leftTotal = total;
		pieChartPosData.clear();
		for (AggregatedRecord record : groupData) {
			pieChartPosData.add(new PieChart.Data(record.getDescription(), (record.getPositive() / total) * 100));
			leftTotal -= record.getPositive();
		}
		pieChartPosData.add(new PieChart.Data("Other", (leftTotal / total) * 100));
		return pieChartPosData;
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
					groupData.addAll(recordStore.groupByDescription(parseArguments(groupBy.getText())));
					toPositivePieChartData();
					toNegativePieChartData();
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
				final List<Record> filtered = recordStore.filter(searchText);
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

	public void fileSelected(final RecordStore recordStore) {
		this.recordStore = recordStore;
		groupBy.setDisable(false);
		search.setDisable(false);
		totalSpent = (int) recordStore.getTotalSpent();
		totalIncome = (int) recordStore.getTotalIncome();
		data.addAll(recordStore.getAllRecords());
		recordStore.printSummary();
	}

	public File showDialog() {
		final DirectoryChooser chooser = new DirectoryChooser();
		return chooser.showDialog(stage);
	}

	public void show() {
		stage.show();
	}

}
