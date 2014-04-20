package org.paulg.ispend.view;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BudgetEntry {

    private StringProperty group;
    private DoubleProperty weekly, monthly, daily;

    public BudgetEntry(String group,
                       double daily,
                       double weekly,
                       double monthly) {
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

    public void setGroup(String group) {
        this.group.set(group);
    }

    public double getWeekly() {
        return weekly.get();
    }

    public DoubleProperty weeklyProperty() {
        return weekly;
    }

    public void setWeekly(double weekly) {
        this.weekly.set(weekly);
    }

    public double getMonthly() {
        return monthly.get();
    }

    public DoubleProperty monthlyProperty() {
        return monthly;
    }

    public void setMonthly(double monthly) {
        this.monthly.set(monthly);
    }

    public double getDaily() {
        return daily.get();
    }

    public DoubleProperty dailyProperty() {
        return daily;
    }

    public void setDaily(double daily) {
        this.daily.set(daily);
    }
}
