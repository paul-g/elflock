package org.paulg.ispend.model;

import java.util.ArrayList;
import java.util.List;

public class AggregatedRecord {

	private String description;
	private double value;
	private int count;
	private double positive;
	private double negative;
    private List<Record> records = new ArrayList<>();

	// XXX dumb!
	public AggregatedRecord(final Record r) {
		super();
		description = r.getDescription();
		value = r.getValue();
		count = 0;
		addRecord(r);
	}

	public AggregatedRecord(final String description, final double value) {
		this.description = description;
		this.value = value;
		count = 0;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	Double getValue() {
		return value;
	}

	public void setValue(final Double value) {
		this.value = value;
	}

	public void addRecord(final Record r) {
		value += r.getValue();
		count++;
		if (r.getValue() < 0) {
			negative += r.getValue();
		} else {
			positive += r.getValue();
		}
        records.add(r);
	}

	int getCount() {
		return count;
	}

	public void setCount(final int count) {
		this.count = count;
	}

	public double getPositive() {
		return positive;
	}

	public void setPositive(final double positive) {
		this.positive = positive;
	}

	public double getNegative() {
		return negative;
	}

	public void setNegative(final double negative) {
		this.negative = negative;
	}

	@Override
	public boolean equals(final Object o) {
		if ((o == null) || !(o instanceof AggregatedRecord)) {
			return false;
		}
		AggregatedRecord ar = (AggregatedRecord) o;
		return (description.equals(ar.getDescription()) && (count == ar.getCount())
				&& (positive == ar.getPositive()) && (negative == ar.getNegative()) && (value == ar
					.getValue()));
	}

	@Override
	public int hashCode() {
		return description.hashCode();
	}

    public List<Record> getRecords() {
        return records;
    }
}
