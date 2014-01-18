package org.paulg.ispend.view;

import java.lang.reflect.Field;
import java.util.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Table view that binds to all fields (public, private and protected) of a provided class.
 **/
class CompleteTableView<T> extends TableView<T> {

    public CompleteTableView(final Class<T> classToBind) {
        final List<TableColumn<T, String>> columns = makeColumns(classToBind);
        getColumns().addAll(columns);
    }

    /** Bind to all declared fields of the provided class. **/
    private List<TableColumn<T, String>> makeColumns(final Class<T> clazz) {
        final List<TableColumn<T, String>> columns = new ArrayList<>();
        for (final Field f : clazz.getDeclaredFields()) {
            final TableColumn<T, String> column = new TableColumn<>(f.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(f.getName()));
            columns.add(column);
        }
        return columns;
    }
}