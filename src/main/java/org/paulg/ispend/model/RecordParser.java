package org.paulg.ispend.model;

import java.nio.file.Path;
import java.util.List;

public interface RecordParser {

    List<Record> parseRecords(Path file);

}
