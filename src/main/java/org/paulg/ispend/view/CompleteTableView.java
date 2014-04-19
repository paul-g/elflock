package org.paulg.ispend.view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Table view that binds to all fields (public, private and protected) of a provided class.
 */
class CompleteTableView<T> extends TableView<T> {

    public CompleteTableView(final Class<T> classToBind) {
        super();
        final List<TableColumn<T, String>> columns = makeColumns(classToBind);
        getColumns().addAll(columns);
    }

    /**
     * Bind to all declared fields of the provided class. *
     */
    private List<TableColumn<T, String>> makeColumns(final Class<T> clazz) {
        final List<TableColumn<T, String>> columns = new ArrayList<>();
        for (final Field f : clazz.getDeclaredFields()) {
            if (f.getAnnotation(IgnoreField.class) != null)
                // skip annotated fields
                continue;
            final TableColumn<T, String> column = new TableColumn<>(f.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(f.getName()));
            columns.add(column);
        }
        return columns;
    }
}
