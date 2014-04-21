package org.paulg.ispend.model;

import org.paulg.ispend.utils.StringUtils;

public class NatWestRecordParser implements RecordParser {

    private String SEP = ",";

    private enum State {
        PARSING_NORMAL, PARSING_QUOTED_ENTRY, END_QUOTED_ENTRY, NOT_PARSING,
    }

    @Override
    public Record parseRecord(final String line) {

        State state = State.PARSING_NORMAL;

        final String[] recordFields = new String[20];
        int fieldCount = 0;

        int pos = 0;
        char[] lchars = line.toCharArray();
        String token = "";
        while (pos < lchars.length) {
            char c = lchars[pos++];
            switch (state) {
                case PARSING_QUOTED_ENTRY:
                    if (c == '"') {
                        if (pos < lchars.length && lchars[pos] != '"') {
                            pos++;
                            state = State.PARSING_NORMAL;
                        }
                        recordFields[fieldCount++] = StringUtils.trimCharacters(token, "\"'");
                        token = "";
                    } else
                        token += c;
                    break;
                case PARSING_NORMAL:
                    if (c == ',') {
                        if (pos < lchars.length && lchars[pos] == '"') {
                            pos++;
                            state = State.PARSING_QUOTED_ENTRY;
                        }
                        recordFields[fieldCount++] = StringUtils.trimCharacters(token, "\"'");
                        token = "";
                    } else
                        token += c;
                    break;
            }
        }

        final Record r = new Record();
        r.setDate(recordFields[0]);
        r.setType(recordFields[1]);
        r.setDescription(recordFields[2]);
        r.setValue(Double.parseDouble(recordFields[3]));
        r.setBalance(Double.parseDouble(recordFields[4]));
        r.setAccountName(recordFields[5]);
        r.setAccountNumber(recordFields[6]);
        return r;
    }
}
