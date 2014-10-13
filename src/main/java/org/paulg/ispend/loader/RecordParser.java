package org.paulg.ispend.loader;

import org.paulg.ispend.model.Record;

import java.nio.file.Path;
import java.util.List;

public interface RecordParser {

    List<Record> parseRecords(Path file);

}
