package org.paulg.ispend.main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.paulg.ispend.store.PreferencesStore;
import org.paulg.ispend.view.ISpendPane;
import org.paulg.ispend.view.utils.UiUtils;

import java.io.File;
import java.io.IOException;

public class ISpend extends Application {

    public static void main(final String[] args) throws IOException {
        launch(args);
    }

    private File workspace = null;
    private TextField t = new TextField("None Selected");

    @Override
    public void start(final Stage stage) {
        // read property file
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(30, 10, 10, 10));
        gp.setVgap(10);
        gp.setHgap(10);
        stage.setTitle("elflock");
        Label welcome = UiUtils.label(
                "elflock stores your session properties" +
                " and encrypted account data in the workspace");
        gp.add(welcome, 1, 0);
        Label l1 = UiUtils.label("Current workspace");

        Button b = new Button("Browse");
        gp.addRow(1, l1, t);
        final Button start = new Button("Start");
        final Button cancel = new Button("Cancel");
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(b, cancel, start);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        gp.addRow(2, new Label(), hbox);

        start.setDisable(true);
        b.setOnAction(event -> {
            workspace = requestWorkspace(stage);
            if (workspace == null)
                return;
            start.setDisable(false);
            t.setText(workspace.getAbsolutePath());
        });

        cancel.setOnAction(event -> System.exit(0));

        start.setOnAction(event -> {
            stage.close();
            PreferencesStore store = new PreferencesStore();
            ISpendPane ipane = new ISpendPane(stage, store);
            ipane.show();
        });

        Scene scene = new Scene(gp);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private File requestWorkspace(final Stage stage) {
        final DirectoryChooser chooser = new DirectoryChooser();
        return chooser.showDialog(stage);
    }
}
