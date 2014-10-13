package org.paulg.ispend.view.drilldown;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.view.widgets.CompleteTableView;

import java.util.List;
import static javafx.collections.FXCollections.observableArrayList;
import static org.paulg.ispend.store.Query.filterAny;

public class SearchView extends VBox {

    private final TextField search;
    private final ObservableList<Record> data;
    private final ObservableList<Record> dataView = observableArrayList();

    public SearchView(ObservableList<Record> data) {
        this.data = data;
        dataView.addAll(data);
        search = new TextField();
        search.setPromptText("Search");
        search.setPrefWidth(400);

        final TableView<Record> recordView = makeTable(this.dataView, Record.class);
        recordView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        getChildren().addAll(search, recordView);
        setVgrow(recordView, Priority.ALWAYS);
        setSpacing(10);
        setPadding(new Insets(5, 5, 5, 5));

        search.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                filterData();
            } else if (t.getCode() == KeyCode.ESCAPE) {
                search.clear();
                filterData();
            }
        });

        this.data.addListener((ListChangeListener<Record>) c -> filterData());
    }

    private void filterData() {
        if (data.size() == 0)
            return;
        final List<Record> filtered = filterAny(data, search.getText());
        dataView.clear();
        dataView.addAll(filtered);
    }

    private <T> TableView<T> makeTable(final ObservableList<T> data,
                                       final Class<T> clazz) {
        final TableView<T> table = new CompleteTableView<>(clazz);
        table.setEditable(true);
        table.setItems(data);
        return table;
    }
}
