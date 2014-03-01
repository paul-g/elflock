package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.model.RecordStore;

import java.util.List;

public class SearchView extends HBox {


    private final TextField search;
    private final ObservableList<Record> data;
    private RecordStore recordStore;

    public SearchView(ObservableList<Record> data) {
        this.data = data;

        search = new TextField();
        search.setPromptText("Search");
        search.setDisable(true);
        search.setPrefWidth(400);

        getChildren().add(search);
    }

    public void setRecordStore(RecordStore recordStore) {
        this.recordStore = recordStore;
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
}
