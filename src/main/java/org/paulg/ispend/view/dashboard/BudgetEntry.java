package org.paulg.ispend.view.dashboard;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BudgetEntry {

    final private StringProperty group;
    final private DoubleProperty weekly, monthly, daily;

    public BudgetEntry(final String group,
                       final double daily,
                       final double weekly,
                       final double monthly) {
        this.group = new SimpleStringProperty(group);
        this.weekly = new SimpleDoubleProperty(weekly);
        this.monthly = new SimpleDoubleProperty(monthly);
        this.daily = new SimpleDoubleProperty(daily);
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

    public double getMonthly() {
        return monthly.get();
    }

    public DoubleProperty monthlyProperty() {
        return monthly;
    }

    public void setMonthly(final double monthly) {
        this.monthly.set(monthly);
    }

    public double getDaily() {
        return daily.get();
    }

    public DoubleProperty dailyProperty() {
        return daily;
    }

    public void setDaily(final double daily) {
        this.daily.set(daily);
    }
}
