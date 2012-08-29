package org.paulg.ispend.main;

public class AggregatedRecord {

	private String description;
	private double value;

	public AggregatedRecord(final Record r) {
		super();
		description = r.getDescription();
		value = r.getValue();
	}

	public AggregatedRecord(final String description, final double value) {
		this.description = description;
		this.value = value;
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
	}

}
