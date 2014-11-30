package org.paulg.ispend.main;

import javafx.application.Application;
import javafx.application.Platform;
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
import org.paulg.ispend.workspace.Workspace;
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
    private final Workspace store = new Workspace();

    @Override
    public void start(final Stage stage) throws IOException {
        store.init();

        Scene scene = makeGui(stage);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private Scene makeGui(Stage stage) {
        stage.setTitle("elflock");

        GridPane gp = makeGrid();
        Label welcome = UiUtils.label(
                "elflock stores your session properties" +
                        " and encrypted account data in the workspace");
        Label lbrowse = UiUtils.label("Current workspace");
        Button browse = new Button("Browse");
        final Button start = new Button("Start");
        final Button cancel = new Button("Cancel");

        HBox buttonPane = buttonPane(browse, start, cancel);

        gp.add(welcome, 1, 0);
        gp.addRow(1, lbrowse, t);
        gp.addRow(2, new Label(), buttonPane);

        // read property file
        String wp = store.getWorkspace();
        if (wp != null) {
            workspace = new File(wp);
            t.setText(wp);
        } else {
            start.setDisable(true);
        }

        browse.setOnAction(event -> {
            workspace = requestWorkspace(stage);
            if (workspace == null)
                return;
            start.setDisable(false);
            t.setText(workspace.getAbsolutePath());
        });

        cancel.setOnAction(event -> Platform.exit());

        start.setOnAction(event -> {
            try {
                store.saveWorkspace(workspace.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.close();
            ISpendPane ipane = new ISpendPane(stage, store);
            ipane.show();
        });
        gp.add(new Label(store.pettyPrint()), 0, 3, 3, 1);
        return new Scene(gp);
    }

    private GridPane makeGrid() {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(30, 10, 10, 10));
        gp.setVgap(10);
        gp.setHgap(10);
        return gp;
    }

    private HBox buttonPane(Button browse, Button start, Button cancel) {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(browse, cancel, start);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        return hbox;
    }

    private File requestWorkspace(final Stage stage) {
        final DirectoryChooser chooser = new DirectoryChooser();
        return chooser.showDialog(stage);
    }
}
