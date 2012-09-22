package org.paulg.ispend.model;

public class Record {

	private String date;
	private String type;
	private String description;
	private String balance;
	private String accountName;
	private String accountNumber;
	private double value;
	private boolean covered;

	public Record() {
		// XXX should probably use a builder
	}

	public Record(final String date, final String type, final String description, final String balance,
			final String accountName, final String accountNumber, final double value) {
		super();
		this.date = date;
		this.type = type;
		this.description = description;
		this.balance = balance;
		this.accountName = accountName;
		this.accountNumber = accountNumber;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(final String balance) {
		this.balance = balance;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(final String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(final String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getValue() {
		return value;
	}

	public void setValue(final double value) {
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return String.format("date: %s, type: %s, description: %s, value: %f", getDate(), getType(), getDescription(),
				getValue());
	}

	@Override
	public boolean equals(final Object o) {
		if ((o == null) || !(o instanceof Record)) {
			return false;
		}
		final Record r = (Record) o;
		return r.getAccountName().equals(accountName) && r.getAccountNumber().equals(accountNumber)
				&& r.getBalance().equals(balance) && r.getDate().equals(date) && r.getDescription().equals(description)
				&& r.getType().equals(type) && (r.getValue() == value);
	}

	@Override
	public int hashCode() {
		// TODO this could probably be improved
		return date.hashCode();
	}

	public void setCovered(final boolean covered) {
		this.covered = covered;
	}

	public boolean isCovered() {
		return covered;
	}
}