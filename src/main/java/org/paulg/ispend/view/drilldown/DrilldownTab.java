package org.paulg.ispend.view.drilldown;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.paulg.ispend.model.Record;

public class DrilldownTab extends Tab {

    public DrilldownTab (String text, final ObservableList<Record> records) {
        super();
        setText(text);
        records.addListener((ListChangeListener<Record>) c -> setText(text + " (" + records.size() + ")"));
        setContent(new SearchView(records));
    }
}
