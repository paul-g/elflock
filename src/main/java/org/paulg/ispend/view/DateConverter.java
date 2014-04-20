package org.paulg.ispend.view;

import javafx.util.StringConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converts from a date given as time in ms since the start of the epoch
 * (java.util.Date#getTime()) to the specified string format and back.
 */
public class DateConverter extends StringConverter<Number> {

    private String dateFormat;

    public DateConverter(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String toString(Number number) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date(number.longValue()));
    }

    @Override
    public Number fromString(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(s).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
