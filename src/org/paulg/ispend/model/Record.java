package org.paulg.ispend.model;

public class Record {

	// XXX use a builder for this class maybe
	private String date;
	private String type;
	private String description;
	private String balance;
	private String accountName;
	private String accountNumber;
	private double value;

	public Record() {
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
}