package org.paulg.ispend.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Observable;
import java.util.Observer;


public class GroupView extends HBox implements Observer {

    private TextField groupBy;

    GroupView(ISpendPane ispendPane) {
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
        getChildren().addAll(groupBy, save);
        setAlignment(Pos.CENTER);
        setHgrow(groupBy, Priority.ALWAYS);
    }

    public void setText(String query) {
        this.groupBy.setText(query);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.groupBy.setDisable(false);
    }
}
