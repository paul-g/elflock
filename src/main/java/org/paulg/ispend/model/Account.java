package org.paulg.ispend.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.joda.time.DateTime;
import org.paulg.ispend.view.widgets.IgnoreField;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class Account {

    @IgnoreField
    private final ObservableList<Record> records = FXCollections.observableArrayList();
    private String number;
    private String name;
    private final IntegerProperty covered = new SimpleIntegerProperty(0);
    private final DoubleProperty coveredPercent = new SimpleDoubleProperty();
    private String firstRecordDate;

    public Account(final String number, final String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getCovered() {
        return covered.get();
    }

    public void setCovered(final int covered) {
        this.covered.set(covered);
        this.coveredPercent.set(records.size() == 0 ?
                0:
                covered / (double)records.size() * 100);
    }

    public int getTotal() {
        return records.size();
    }

    public double getBalance() {
        return Collections.max(records).getBalance();
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == null || !(o instanceof Account) || number == null || name == null) {
            return false;
        }
        Account a = (Account) o;
        return number.equals(a.getNumber()) && name.equals(a.getName());
    }

    public void printSummary() {
        double maxNegative = Double.MAX_VALUE;
        Record maxNegativeRecord = null;
        double totalNegative = 0;
        double totalPositive = 0;
        int negatives = 0;
        int positives = 0;
        for (final Record r : records) {
            if (r.getValue() < 0) {
                totalNegative += r.getValue();
                negatives++;

                if (r.getValue() < maxNegative) {
                    maxNegative = r.getValue();
                    maxNegativeRecord = r;
                }

            } else {
                totalPositive += r.getValue();
                positives++;
            }

        }

        System.out.println("For account number " + name + " name : " + name);
        System.out.println("\tTotal records: " + records.size());
        System.out.println("\tTotal negative:" + totalNegative + " avg: " + (totalNegative / negatives));
        System.out.println("\tTotal positive:" + totalPositive + " avg: " + (totalPositive / positives));
        System.out.println("\tMaximum negative record: " + maxNegativeRecord);
        System.out.println("\tFlow: " + (totalPositive + totalNegative));
    }

    public void addRecord(final Record r) {
        records.add(r);
    }

    public List<Record> getRecords() {
        return records;
    }

    public String getLastRecordDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        DateTime lastRecord = Collections.max(records).getDate();
        return sdf.format(lastRecord.toDate());
    }

    public int getTotalRecords() {
        return records.size();
    }

    public IntegerProperty getCoveredStringProperty() {
        return covered;
    }

    public DoubleProperty getCoveredPercentProperty() {
        return coveredPercent;
    }

    public String getFirstRecordDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        DateTime lastRecord = Collections.min(records).getDate();
        return sdf.format(lastRecord.toDate());
    }
}
