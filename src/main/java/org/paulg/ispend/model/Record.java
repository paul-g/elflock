package org.paulg.ispend.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class Record implements Comparable<Record> {

    private final DateTime date;
    private final String type;
    private final String description;
    private final double value;
    private final Double balance;
    private final String accountName;
    private final String accountNumber;
    private boolean covered;

    @Override
    public int hashCode() {
        // TODO this could probably be improved
        return date.hashCode();
    }

    @Override
    public int compareTo(Record o) {
        if (o == null) return -1;
        return date.compareTo(o.getDate());
    }
}
