package org.paulg.ispend.model;

import org.paulg.ispend.utils.StringUtils;

import java.util.Arrays;
import java.util.StringTokenizer;

public class NatWestRecordParser implements RecordParser {

    private String SEP = ",";

    private enum State  {
        PARSING_NORMAL, PARSING_QUOTED_ENTRY, END_QUOTED_ENTRY,
    }

	@Override
	public Record parseRecord(final String line) {

        State state = State.PARSING_NORMAL;
        StringTokenizer st = new StringTokenizer(line, SEP);

        final String[] recordFields = new String[20];
        String entry = "";
        int fieldCount = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.isEmpty())
                // deals with trailing comma
                continue;
            state = token.startsWith("\"") ? State.PARSING_QUOTED_ENTRY : state;
            state = token.endsWith("\"") ? State.END_QUOTED_ENTRY : state;
            switch (state) {
                case PARSING_QUOTED_ENTRY:
                    entry += token + ",";
                    break;
                case END_QUOTED_ENTRY:
                    entry += token;
                    state = State.PARSING_NORMAL;
                    recordFields[fieldCount++] = StringUtils.trimCharacters(entry, "\"'");
                    entry = "";
                    break;
                case PARSING_NORMAL:
                    entry = token;
                    recordFields[fieldCount++] = StringUtils.trimCharacters(entry, "\"'");
                    entry = "";
                    break;
            }

        }

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
