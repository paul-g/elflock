package org.paulg.ispend.model;

import org.jfree.data.time.TimeSeries;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.paulg.ispend.store.RecordStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.paulg.ispend.store.Query.filterAny;

public class RecordStoreTest {

    private static final double E = 0.00001;
    private static final String ACC1 = "123-444";
    private static final String ACC2 = "123-445";
    private RecordStore store;
    private Record r, r1, r2;
    private AggregatedRecord ar1;
    private final List<Record> testrecords = new ArrayList<>();

    private DateTime pd(String date) {
        return DateTime.parse(date, DateTimeFormat.forPattern("dd/MM/yyyy"));
    }

    @Before
    public void setUp() {
        store = new RecordStore();
        r1 = new Record(pd("04/02/2013"), "type", "transaction 1", 3.75, 0., "account", ACC1);
        store.addRecord(r1);
        r2 = new Record(pd("04/02/2013"), "type", "transaction 2", 5.11, 0., "account", ACC1);
        store.addRecord(r2);
        r = new Record(pd("11/02/2013"), "type", "trans 2", -2.11, 0., "account", ACC2);
        store.addRecord(r);
        ar1 = new AggregatedRecord("transaction", 0);
        ar1.addRecord(r1);
        ar1.addRecord(r2);
        Collections.addAll(testrecords, r1, r2, r);
    }

    @Test
    public void testStoreRecord() {
        List<Record> records = store.getRecordsByAccountNumber(ACC1);
        assertArrayEquals(new Record[]{r1, r2}, records.toArray());
        List<Record> allRecords = store.getAllRecords();
        assertArrayEquals(new Record[]{r1, r2, r}, allRecords.toArray());
    }

    @Test
    public void testFilter() {
        List<Record> records = filterAny(testrecords, "transaction");
        assertArrayEquals(new Record[]{r1, r2}, records.toArray());
    }

    @Test
    public void testStatistics() {
        double expectedIncome = r1.getValue() + r2.getValue();
        assertEquals(expectedIncome, store.getTotalIncome(), E);
        double expectedSpent = Math.abs(r.getValue());
        assertEquals(expectedSpent, store.getTotalSpent(), E);
    }

    @Test
    public void testAggregators() {
        TimeSeries tsData = store.getWeeklyAveragesByDescription("trans");
        Double d1Average = tsData.getDataItem(0).getValue().doubleValue();
        Double d2Average = tsData.getDataItem(1).getValue().doubleValue();

        // check average for each week
        assertEquals((r1.getValue() + r2.getValue()) / 2, d1Average, 1e-10);
        assertEquals(r.getValue(), d2Average, 1e-10);

        // check average for all weeks
        Double average = store.getWeeklyAverageByDescription("trans");
        assertEquals(((r1.getValue() + r2.getValue()) / 2 + r.getValue()) / 2, average, 1e-10);
    }

    @Test
    public void testGrouping() {
        List<AggregatedRecord> records = store.groupByDescription("transaction");
        assertArrayEquals(new AggregatedRecord[]{ar1}, records.toArray());
        records = store.groupByDescription("non-existent");
        AggregatedRecord ar = new AggregatedRecord("non-existent", 0);
        assertArrayEquals(new AggregatedRecord[]{ar}, records.toArray());
    }

}
