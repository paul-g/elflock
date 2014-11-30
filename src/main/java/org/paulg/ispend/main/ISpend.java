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
import java.nio.file.Path;

public class ISpend extends Application {

    public static void main(final String[] args) throws IOException {
        launch(args);
    }

    private TextField t = new TextField("None Selected");
    private Path workspacePath;
    private Stage stage;

    @Override
    public void start(final Stage stage) throws IOException {
        this.workspacePath = Workspace.getSavedWorkspace();
        this.stage = stage;

        Scene scene = makeGui();

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void loadPane() {
        try {
            Workspace workspace = Workspace.open(workspacePath);
            stage.close();
            ISpendPane ipane = new ISpendPane(stage, workspace);
            ipane.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setNewWorkspace() {
        workspacePath = requestWorkspace(stage).toPath();
        if (workspacePath == null)
            return;
        t.setText(workspacePath.toString());
    }

    private Scene makeGui() {
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

        if (workspacePath == null) {
            start.setDisable(true);
        } else {
            t.setText(workspacePath.toString());
        }

        browse.setOnAction(event -> {
            setNewWorkspace();
            start.setDisable(false);
        });
        cancel.setOnAction(event -> Platform.exit());
        start.setOnAction(event -> loadPane());

        //gp.add(new Label(store.pettyPrint()), 0, 3, 3, 1);
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
