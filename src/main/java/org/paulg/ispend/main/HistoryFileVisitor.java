package org.paulg.ispend.main;

import org.paulg.ispend.loader.NatWestRecordParser;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.loader.RecordParser;
import org.paulg.ispend.store.InMemoryRecordStore;
import org.paulg.ispend.store.RecordStore;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public final class HistoryFileVisitor extends SimpleFileVisitor<Path> {

    private final RecordStore recordStore;
    private RecordParser recordParser;

    public HistoryFileVisitor() {
        recordStore = new InMemoryRecordStore();
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
