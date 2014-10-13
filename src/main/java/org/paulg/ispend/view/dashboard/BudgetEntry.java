package org.paulg.ispend.view.dashboard;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BudgetEntry {

    final private StringProperty group;
    final private DoubleProperty weekly;

    public BudgetEntry(final String group,
                       final double weekly
                       ) {
        this.group = new SimpleStringProperty(group);
        this.weekly = new SimpleDoubleProperty(weekly);
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
}
