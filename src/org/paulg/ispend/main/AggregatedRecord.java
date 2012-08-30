package org.paulg.ispend.main;

public class AggregatedRecord {

	private String description;
	private double value;
	private int count;
	private double positive;
	private double negative;

	public AggregatedRecord(final Record r) {
		super();
		description = r.getDescription();
		value = r.getValue();
		count = 0;
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

	public Double getValue() {
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
	}

	public int getCount() {
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

}
