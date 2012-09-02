package org.paulg.ispend.main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import org.paulg.ispend.model.*;

public final class HistoryFileVisitor extends SimpleFileVisitor<Path> {

	private final RecordStore recordStore;
	private RecordParser recordParser;

	public HistoryFileVisitor() {
		recordStore = new RecordStore();
	}

	public RecordStore getRecordStore() {
		return recordStore;
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
				// TODO parse header - assume nat west for now
				recordParser = new NatWestRecordParser();
			} else if (!line.isEmpty()) {
				recordStore.addRecord(recordParser.parseRecord(line));
			}
		}

		return FileVisitResult.CONTINUE;
	}
}