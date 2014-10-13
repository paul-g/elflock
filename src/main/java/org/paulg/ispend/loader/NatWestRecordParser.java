package org.paulg.ispend.loader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.paulg.ispend.loader.RecordParser;
import org.paulg.ispend.model.Record;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NatWestRecordParser implements RecordParser {

    @Override
    public List<Record> parseRecords(final Path path) {

        List<Record> records = new ArrayList<>();
        try {
            CSVParser parser = CSVParser.parse(path.toFile(),
                    Charset.defaultCharset(),
                    CSVFormat.DEFAULT.withHeader());
            for (CSVRecord csvRecord : parser) {
                final Record r = new Record(
                        parseDate(csvRecord.get("Date")),
                        csvRecord.get("Type"),
                        csvRecord.get("Description"),
                        Double.parseDouble(csvRecord.get("Value")),
                        Double.parseDouble(csvRecord.get("Balance")),
                        csvRecord.get("Account Name"),
                        csvRecord.get("Account Number"));
                records.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private DateTime parseDate(String date) {
        return DateTime.parse(date, DateTimeFormat.forPattern("dd/MM/yyyy"));
    }
}
