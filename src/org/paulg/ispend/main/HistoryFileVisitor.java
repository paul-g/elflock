package org.paulg.ispend.main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import org.paulg.ispend.model.*;

public final class HistoryFileVisitor extends SimpleFileVisitor<Path> {

	private final RecordParser recordParser;

	public HistoryFileVisitor(final RecordParser recordParser) {
		this.recordParser = recordParser;
	}

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
				final Record r = RecordParser.parseRecord(line);
				if (r != null) {
					List<Record> records = recordParser.getRecordsByAccountName(r.getAccountName());
					if (records == null) {
						records = new ArrayList<Record>();
						recordParser.addRecord(r.getAccountName(), records);
					}
					records.add(r);
				}
			}
		}

		return FileVisitResult.CONTINUE;
	}
}