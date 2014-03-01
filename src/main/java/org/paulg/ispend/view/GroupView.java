package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.paulg.ispend.model.AggregatedRecord;

import java.util.Observable;
import java.util.Observer;


public class GroupView extends VBox implements Observer {

    private TextField groupBy;

    GroupView(ISpendPane ispendPane, ObservableList<AggregatedRecord> groupData) {
        groupBy = new TextField();
        groupBy.setPromptText("Group byyy");
        groupBy.setDisable(true);
        groupBy.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                ispendPane.setQuery(groupBy.getText());
            } else if (t.getCode() == KeyCode.ESCAPE) {
                ispendPane.clearQuery();
            }
        });

        Button save = new Button();
        save.setText("Save");
        save.setOnAction(event -> ispendPane.saveQuery(groupBy.getText()));

        final TableView<AggregatedRecord> aggregatedRecordView = makeTable(groupData,
                AggregatedRecord.class);

        HBox hbox = new HBox();
        hbox.setHgrow(groupBy, Priority.ALWAYS);
        Label label = new Label("Group By: ");
        hbox.getChildren().addAll(label, groupBy, save);
        hbox.setAlignment(Pos.CENTER);
        getChildren().addAll(hbox, aggregatedRecordView);
    }

    public void setText(String query) {
        this.groupBy.setText(query);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.groupBy.setDisable(false);
    }

    private <T> TableView<T> makeTable(final ObservableList<T> data, final Class<T> clazz) {
        final TableView<T> table = new CompleteTableView<>(clazz);
        table.setEditable(true);
        table.setItems(data);
        return table;
    }
}
