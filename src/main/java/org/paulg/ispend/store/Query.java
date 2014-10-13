package org.paulg.ispend.store;

import org.paulg.ispend.model.Record;
import org.paulg.ispend.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Query {

    /** Returns the records matching any of the query atoms. Multiple query atoms
     *  are separated by comm2a. */
    public static List<Record> filterAny(List<Record> records, final String query) {
        // XXX this will include duplicates for records matching more than one query atom
        // need to use a set, pending correct equals implementation in Record
        List<String> atoms = splitQuery(query);
        List<Record> any = new ArrayList<>();
        for (String atom : atoms) {
            any.addAll(filterAtom(records, atom));
        }
        return any;
    }

    public static List<String> splitQuery(String query) {
        String[] atoms = query.split(",");
        List<String> as = new ArrayList<>();
        for (String s : atoms) {
            as.add(s.trim());
        }
        return as;
    }

    private static List<Record> filterAtom(List<Record> records, final String text) {
        return records.stream()
                .filter(r -> StringUtils.containsIgnoreCase(r.getDescription(), text))
                .collect(Collectors.toList());
    }
}
