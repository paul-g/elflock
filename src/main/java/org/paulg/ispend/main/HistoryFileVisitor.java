package org.paulg.ispend.main;

import org.paulg.ispend.model.NatWestRecordParser;
import org.paulg.ispend.model.RecordParser;
import org.paulg.ispend.model.RecordStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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
    public FileVisitResult visitFile(final Path file,
                                     final BasicFileAttributes attrs) throws IOException {
        if (!file.toString().endsWith("csv")) {
            return FileVisitResult.CONTINUE;
        }

        final BufferedReader br = Files.newBufferedReader(file, Charset.defaultCharset());

        String line = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("Date")) {
                // TODO parse header and check record type - assume nat west for now
                recordParser = new NatWestRecordParser();
            } else if (!line.isEmpty()) {
                recordStore.addRecord(recordParser.parseRecord(line));
            }
        }

        return FileVisitResult.CONTINUE;
    }
}
