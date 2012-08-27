package org.paulg.ispend.main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class RecordParser {

	private final Map<String, List<Record>> recordsByAccountName = new HashMap<String, List<Record>>();
	private final Path path;

	public RecordParser(final Path path) throws IOException {
		this.path = path;
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
		Files.walkFileTree(path, new HistoryFileVisitor());
	}

	private static Record parseRecord(final String line) {

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

	private final class HistoryFileVisitor extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
			final BufferedReader br = Files.newBufferedReader(file, Charset.defaultCharset());
			String line = null;
			System.out.println("File: " + file.toString());

			while ((line = br.readLine()) != null) {
				// /System.out.println(line);
				line = line.trim();
				if (line.startsWith("Date")) {
					// TODO parse header
				} else if (!line.isEmpty()) {
					final Record r = parseRecord(line);
					if (r != null) {
						List<Record> records = recordsByAccountName.get(r.getAccountName());
						if (records == null) {
							records = new ArrayList<Record>();
							recordsByAccountName.put(r.getAccountName(), records);
						}
						records.add(r);
					}
				}
			}

			return FileVisitResult.CONTINUE;
		}
	}

	public List<Record> getAllRecords() {
		final List<Record> allRecords = new ArrayList<Record>();
		for (final List<Record> rs : recordsByAccountName.values()) {
			allRecords.addAll(rs);
		}
		return allRecords;
	}
}
