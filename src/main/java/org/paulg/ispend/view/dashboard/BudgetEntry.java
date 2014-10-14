package org.paulg.ispend.view.dashboard;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BudgetEntry {

    final private StringProperty group;
    final private StringProperty label;
    final private DoubleProperty weekly;

    public BudgetEntry(final String group,
                       final String label,
                       final double weekly
                       ) {
        this.group = new SimpleStringProperty(group);
        this.weekly = new SimpleDoubleProperty(weekly);
        this.label = new SimpleStringProperty(label);
    }

    public String getGroup() {
        return group.get();
    }

    public StringProperty groupProperty() {
        return group;
    }

    public void setGroup(final String group) {
        this.group.set(group);
    }

    public double getWeekly() {
        return weekly.get();
    }

    public DoubleProperty weeklyProperty() {
        return weekly;
    }

    public void setWeekly(final double weekly) {
        this.weekly.set(weekly);
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public String getLabel() {
        return label.get();
    }
}
