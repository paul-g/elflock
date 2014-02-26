package org.paulg.ispend.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NatWestRecordParserTest {

	private final static String record = "29/03/2011,POS,\"'8854 28MAR11 , MARGARET MILL , LONDON GB\",-9.00,6.07,\"'curent\",\"'515001-64829197\",";

	@Test
	public void test() {
		final RecordParser rp = new NatWestRecordParser();
		final Record r = rp.parseRecord(record);
		final Record expected = new Record("29/03/2011", "POS", "8854 28MAR11 , MARGARET MILL , LONDON GB", 6.07,
				"curent", "515001-64829197", -9.00);
		assertEquals("Constructed record matches parsed record", expected, r);
	}

}

