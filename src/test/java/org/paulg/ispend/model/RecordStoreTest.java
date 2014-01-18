package org.paulg.ispend.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

public class RecordStoreTest {

	private static final double E = 0.00001;
	private static final String ACC1 = "123-444";
	private static final String ACC2 = "123-445";
	private RecordStore store;
	private Record r, r1, r2;
	private AggregatedRecord ar1;

	@Before
	public void setUp() {
		store = new RecordStore();
		r1 = new Record("01/02/2013", "type", "transaction 1", "0", "account", ACC1, 3.75);
		store.addRecord(r1);
		r2 = new Record("01/02/2013", "type", "transaction 2", "0", "account", ACC1, 5.11);
		store.addRecord(r2);
		r = new Record("01/02/2013", "type", "trans 2", "0", "account", ACC2, -2.11);
		store.addRecord(r);
		ar1 = new AggregatedRecord("transaction", 0);
		ar1.addRecord(r1);
		ar1.addRecord(r2);
	}

	@Test
	public void testStoreRecord() {
		List<Record> records = store.getRecordsByAccountNumber(ACC1);
		assertArrayEquals(new Record[] { r1, r2 }, records.toArray());
		List<Record> allRecords = store.getAllRecords();
		assertArrayEquals(new Record[] { r1, r2, r }, allRecords.toArray());
	}

	@Test
	public void testFilter() {
		List<Record> records = store.filter("transaction");
		assertArrayEquals(new Record[] { r1, r2 }, records.toArray());
	}

	@Test
	public void testStatistics() {
		double expectedIncome = r1.getValue() + r2.getValue();
		assertEquals(expectedIncome, store.getTotalIncome(), E);
		double expectedSpent = Math.abs(r.getValue());
		assertEquals(expectedSpent, store.getTotalSpent(), E);
	}

	@Test
	public void testGrouping() {
		List<AggregatedRecord> records = store.groupByDescription("transaction");
		assertArrayEquals(new AggregatedRecord[] { ar1 }, records.toArray());
		records = store.groupByDescription("non-existent");
		AggregatedRecord ar = new AggregatedRecord("non-existent", 0);
		assertArrayEquals(new AggregatedRecord[] { ar }, records.toArray());
	}

}
