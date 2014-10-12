package org.paulg.ispend.main;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.paulg.ispend.model.NatWestRecordParser;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.model.RecordParser;
import org.paulg.ispend.model.RecordStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

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

        List<Record > rs = new NatWestRecordParser().parseRecords(file);
        rs.forEach(recordStore::addRecord);
        return FileVisitResult.CONTINUE;
    }
}
