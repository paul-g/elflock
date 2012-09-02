package org.paulg.ispend.model;

import org.paulg.ispend.utils.StringUtils;

public class NatWestRecordParser implements RecordParser {

	@Override
	public Record parseRecord(final String line) {
		final String[] oldFields = line.split(",");
		final String[] recordFields = new String[7];
		int fieldCount = 0;
		for (int i = 0; i < oldFields.length; i++) {
			String field = "";
			if (oldFields[i].startsWith("\"")) {
				while (!oldFields[i].endsWith("\"")) {
					field += oldFields[i];
					field += ",";
					i++;
				}
			}
			field += oldFields[i];
			recordFields[fieldCount++] = StringUtils.trimCharacters(field, "\"'");
		}

		// XXX use a builder here maybe?
		final Record r = new Record();
		r.setDate(recordFields[0]);
		r.setType(recordFields[1]);
		r.setDescription(recordFields[2]);
		r.setValue(Double.parseDouble(recordFields[3]));
		r.setBalance(recordFields[4]);
		r.setAccountName(recordFields[5]);
		r.setAccountNumber(recordFields[6]);

		return r;
	}
}
