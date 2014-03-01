package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.model.RecordStore;

import java.util.List;

public class SearchView extends VBox {


    private final TextField search;
    private final ObservableList<Record> data;


    public SearchView(ObservableList<Record> data) {
        this.data = data;
        search = new TextField();
        search.setPromptText("Search");
        search.setDisable(true);
        search.setPrefWidth(400);

        final TableView<Record> recordView = makeTable(data, Record.class);

        getChildren().addAll(search, recordView);
        setVgrow(recordView, Priority.ALWAYS);
        setSpacing(10);
        setPadding(new Insets(5, 5, 5, 5));
    }

    public void setRecordStore(RecordStore recordStore) {
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
        this.search.setDisable(false);
    }

    private <T> TableView<T> makeTable(final ObservableList<T> data,
                                       final Class<T> clazz) {
        final TableView<T> table = new CompleteTableView<>(clazz);
        table.setEditable(true);
        table.setItems(data);
        return table;
    }
}
