package org.paulg.ispend.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NatWestRecordParser implements RecordParser {

    @Override
    public List<Record> parseRecords(final Path path) {

        CSVParser parser = null;
        try {
            parser = CSVParser.parse(path.toFile(),
                    Charset.defaultCharset(),
                    CSVFormat.EXCEL.withHeader("Date", "Type", "Description",
                            "Value", "Balance", "Account Name", "Account Number"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Record> records = new ArrayList<>();
        for (CSVRecord csvRecord : parser) {
            final Record r = new Record();
            r.setDate(csvRecord.get("Date"));
            r.setType(csvRecord.get("Type"));
            r.setDescription(csvRecord.get("Description"));
            r.setValue(Double.parseDouble(csvRecord.get("Value")));
            r.setBalance(Double.parseDouble(csvRecord.get("Balance")));
            r.setAccountName(csvRecord.get("Account Name"));
            r.setAccountNumber(csvRecord.get("Account Number"));
            records.add(r);
        }
        return records;
    }
}
