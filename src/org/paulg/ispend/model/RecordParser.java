package org.paulg.ispend.model;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.paulg.ispend.main.HistoryFileVisitor;
import org.paulg.ispend.utils.StringUtils;

public class RecordParser {

	final Map<String, List<Record>> recordsByAccountName = new HashMap<String, List<Record>>();
	private final Path path;

	public RecordParser(final Path rootDirectory) throws IOException {
		path = rootDirectory;
		getRecordsFromHistoryFiles(); // XXX bad idea
	}

	public synchronized List<Record> getRecordsByAccountName(final String accountName) {
		return new ArrayList<Record>(recordsByAccountName.get(accountName));
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
		if (records == null)
			return;
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

	private void getRecordsFromHistoryFiles() throws IOException {
		Files.walkFileTree(path, new HistoryFileVisitor(this));
	}

	public static Record parseRecord(final String line) {

		final String[] oldFields = line.split(",");
		final String[] recordFields = new String[7];
		int fieldCount = 0;
		for (int i = 0; i < oldFields.length; i++) {
			String field = "";
			if (oldFields[i].startsWith("\"")) {
				while (!oldFields[i].endsWith("\"")) {
					field += oldFields[i];
					i++;
				}
			}
			field += oldFields[i];
			recordFields[fieldCount++] = field;
		}

		// XXX use a builder here maybe?
		final Record r = new Record();
		r.setDate(recordFields[0]);
		r.setType(recordFields[1]);
		r.setDescription(recordFields[2]);
		r.setValue(Double.parseDouble(recordFields[3]));
		r.setBalance(recordFields[4]);
		r.setAccountName(recordFields[5]);
		r.setAccountNumber(recordFields[6]);

		return r;
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
		for (final Record r : unfiltered)
			if (StringUtils.containsIgnoreCase(r.getDescription(), text)) {
				filtered.add(r);
			}
		return filtered;
	}

	public List<AggregatedRecord> groupByDescription(final String... groupTags) {
		final List<AggregatedRecord> tagRecords = new ArrayList<AggregatedRecord>();
		if ((groupTags != null) && (groupTags.length > 0)) {
			for (String tag : groupTags) {
				tag = tag.trim();
				final AggregatedRecord tagRecord = new AggregatedRecord(tag, 0);
				for (final Record r : getAllRecords())
					if (StringUtils.containsIgnoreCase(r.getDescription(), tag)) {
						tagRecord.addRecord(r);
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
