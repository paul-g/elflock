package org.paulg.ispend.model;

import java.util.*;

import org.jfree.data.time.*;
import org.paulg.ispend.utils.StringUtils;

public class RecordStore {

	private final Map<String, Account> accounts = new LinkedHashMap<>();

	public void addRecord(final Record r) {
		Account acc = accounts.get(r.getAccountNumber());
		if (acc == null) {
			acc = new Account(r.getAccountNumber(), r.getAccountName());
			accounts.put(r.getAccountNumber(), acc);
		}
		acc.addRecord(r);
	}

	public List<Record> getRecordsByAccountNumber(final String number) {
		if (accounts.get(number) != null) {
			return new ArrayList<>(accounts.get(number).getRecords());
		}
		return new ArrayList<>();
	}

	public void printSummary() {
		for (final Account a : accounts.values()) {
			a.printSummary();
		}
	}

	public List<Record> getAllRecords() {
		final List<Record> allRecords = new ArrayList<>();
		for (final Account a : accounts.values()) {
			allRecords.addAll(a.getRecords());
		}
		return allRecords;
	}

	public List<Record> filter(final String text) {
		final List<Record> unfiltered = getAllRecords();
		final List<Record> filtered = new ArrayList<>();
		for (final Record r : unfiltered) {
			if (StringUtils.containsIgnoreCase(r.getDescription(), text)) {
				filtered.add(r);
			}
		}
		return filtered;
	}

	private String[] parseArguments(final String text) {
		return text.split(",");
	}

	public List<AggregatedRecord> groupByDescription(final String query) {
		String[] tags = parseArguments(query);
		final List<AggregatedRecord> tagRecords = new ArrayList<>();
        if (query.isEmpty())
            return tagRecords;

		if ((tags != null) && (tags.length > 0)) {

			for (Account a : accounts.values()) {
				a.setCovered(0);
			}

			for (String tag : tags) {
				tag = tag.trim();
				final AggregatedRecord tagRecord = new AggregatedRecord(tag, 0);
				for (Account a : accounts.values()) {
					for (final Record r : a.getRecords()) {
						if (StringUtils.containsIgnoreCase(r.getDescription(), tag)) {
							tagRecord.addRecord(r);
							r.setCovered(true);
							a.setCovered(a.getCovered() + 1);
						} else {
							r.setCovered(false);
						}
					}
				}
				tagRecords.add(tagRecord);
			}
		}
		return tagRecords;
	}

	public double getTotalIncome() {
		double income = 0;
		for (Account a : accounts.values()) {
			for (Record r : a.getRecords()) {
				if (r.getValue() > 0) {
					income += r.getValue();
				}
			}
		}
		return income;
	}

	public double getTotalSpent() {
		double spent = 0;
		for (Account a : accounts.values()) {
			for (Record r : a.getRecords()) {
				if (r.getValue() < 0) {
					spent += Math.abs(r.getValue());
				}
			}
		}
		return spent;
	}

	public Collection<Account> getAccounts() {
		return accounts.values();
	}

    public Map<Date, Double> getWeeklyBalance() {
        return getBalance(Calendar.WEEK_OF_YEAR);
    }

    public Map<Date, Double> getMonthlyBalance() {
        return getBalance(Calendar.MONTH);
    }

    private Map<Date, Double> getBalance(int period) {

        System.out.println("Using JFREE");
        TimeSeries ts = getRecords(period);
        Map<Date, Double> newMap = new HashMap<>();
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem it = ts.getDataItem(i);
            newMap.put(it.getPeriod().getStart(), it.getValue().doubleValue());
        }
            return newMap;

    }

    /** Get records grouped in a certain unit of time (e.g. records per
     * Calendar.MONTH or Calendar.WEEK etc.). This can be used for weekly/monthly
     * statistics */
    private TimeSeries getRecords(int period) {
        List<List<Record>> allRecords = new ArrayList<>();

        Map<RegularTimePeriod, Integer> periodCount = new HashMap<>();

        TimeSeries ts = new TimeSeries("Test");
        for (Account a : accounts.values()) {
            List<Record> rs = a.getRecords();
            for (Record r : rs) {
                // aggregate
                RegularTimePeriod timePeriod;
                if (period == Calendar.MONTH)
                    timePeriod = new Month(r.getDate());
                else
                    timePeriod = new Week(r.getDate());
                TimeSeriesDataItem tsItem = ts.getDataItem(timePeriod);
                double oldValue = tsItem == null ? 0 : tsItem.getValue().doubleValue();
                Integer count = periodCount.get(timePeriod);
                count = count == null ? 1 : count + 1;

                // XXX this leads to some (acceptable for now) loss of accuracy
                double newValue = (oldValue * (count - 1) + r.getBalance()) / count;
                ts.addOrUpdate(timePeriod, newValue);
            }
            allRecords.add(rs);
        }

        for (Object item : ts.getItems()) {
            TimeSeriesDataItem tsItem = (TimeSeriesDataItem)item;
            System.out.println(tsItem.getPeriod() + " " + tsItem.getValue());
        }
        return ts;
    }
}
