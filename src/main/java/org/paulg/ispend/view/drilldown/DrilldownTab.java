package org.paulg.ispend.view.drilldown;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.paulg.ispend.model.Record;

public class DrilldownTab extends Tab {

    private final ObservableList<Record> records;
    private final String text;

    public DrilldownTab (String text, final ObservableList<Record> records) {
        super();
        this.records = records;
        this.text = text;
        setTabText();
        records.addListener((ListChangeListener<Record>) c -> setTabText());
        setContent(new SearchView(records));
    }

    private void setTabText() {
        setText(text + " (" + records.size() + ")");
    }
}
