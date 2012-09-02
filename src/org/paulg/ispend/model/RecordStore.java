package org.paulg.ispend.model;

import java.util.*;

import org.paulg.ispend.utils.StringUtils;

public class RecordStore {

	final Map<String, List<Record>> recordsByAccountName = new HashMap<String, List<Record>>();

	public List<Record> getRecordsByAccountName(final String accountName) {
		if (recordsByAccountName.get(accountName) != null) {
			return new ArrayList<Record>(recordsByAccountName.get(accountName));
		}
		return new ArrayList<Record>();
	}

	public void addRecord(final Record r) {
		List<Record> records = recordsByAccountName.get(r.getAccountName());
		if (records == null) {
			records = new ArrayList<Record>();
			addRecord(r.getAccountName(), records);
		}
		records.add(r);
	}

	public void printSummary() {
		for (final String s : recordsByAccountName.keySet()) {
			printSummaryByAccount(s);
		}
	}

	public void printSummaryByAccount(final String accountName) {
		double maxNegative = Double.MAX_VALUE;
		Record maxNegativeRecord = null;
		final List<Record> records = recordsByAccountName.get(accountName);
		if (records == null) {
			return;
		}
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

		System.out.println("For account " + accountName);
		System.out.println("\tTotal records: " + records.size());
		System.out.println("\tTotal negative:" + totalNegative + " avg: " + (totalNegative / negatives));
		System.out.println("\tTotal positive:" + totalPositive + " avg: " + (totalPositive / positives));
		System.out.println("\tMaximum negative record: " + maxNegativeRecord);
		System.out.println("\tFlow: " + (totalPositive + totalNegative));
	}

	public List<Record> getAllRecords() {
		final List<Record> allRecords = new ArrayList<Record>();
		for (final List<Record> rs : recordsByAccountName.values()) {
			allRecords.addAll(rs);
		}
		return allRecords;
	}

	public List<Record> filter(final String text) {
		final List<Record> unfiltered = getAllRecords();
		final List<Record> filtered = new ArrayList<Record>();
		for (final Record r : unfiltered) {
			if (StringUtils.containsIgnoreCase(r.getDescription(), text)) {
				filtered.add(r);
			}
		}
		return filtered;
	}

	public List<AggregatedRecord> groupByDescription(final String... groupTags) {
		final List<AggregatedRecord> tagRecords = new ArrayList<AggregatedRecord>();
		if ((groupTags != null) && (groupTags.length > 0)) {
			for (String tag : groupTags) {
				tag = tag.trim();
				final AggregatedRecord tagRecord = new AggregatedRecord(tag, 0);
				for (final Record r : getAllRecords()) {
					if (StringUtils.containsIgnoreCase(r.getDescription(), tag)) {
						tagRecord.addRecord(r);
					}
				}
				tagRecords.add(tagRecord);
			}
		}
		return tagRecords;
	}

	public void addRecord(final String accountName, final List<Record> records) {
		recordsByAccountName.put(accountName, records);
	}
}
