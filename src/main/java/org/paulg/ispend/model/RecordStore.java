package org.paulg.ispend.model;

import org.jfree.data.time.*;
import org.paulg.ispend.utils.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class RecordStore {

    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public void addRecord(final Record r) {
        Account acc = accounts.get(r.getAccountNumber());
        if (acc == null) {
            acc = new Account(r.getAccountNumber(), r.getAccountName());
            accounts.put(r.getAccountNumber(), acc);
        }
        acc.addRecord(r);
    }

    public List<Record> getRecordsByAccountNumber(final String number) {
        if (accounts.get(number) != null) {
            return new ArrayList<>(accounts.get(number).getRecords());
        }
        return new ArrayList<>();
    }

    /** Returns the records matching any of the query atoms. Multiple query atoms
     *  are separated by comma. */
    public List<Record> filterAny(final String query) {
        // XXX this will include duplicates for records matching more than one query atom
        // need to use a set, pending correct equals implementation in Record
        List<String> atoms = splitQuery(query);
        List<Record> any = new ArrayList<>();
        for (String atom : atoms) {
            any.addAll(filterAtom(atom));
        }
        return any;
    }

    private List<String> splitQuery(String query) {
        String[] atoms = query.split(",");
        List<String> as = new ArrayList<>();
        for (String s : atoms) {
            as.add(s.trim());
        }
        return as;
    }

    public List<Record> filterAll() {
        throw new RuntimeException("RecordStore#filterAll() not implemented!");
    }

    private List<Record> filterAtom(final String text) {
        final List<Record> unfiltered = getAllRecords();
        final List<Record> filtered = new ArrayList<>();
        for (final Record r : unfiltered) {
            if (StringUtils.containsIgnoreCase(r.getDescription(), text)) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    private String[] parseArguments(final String text) {
        return text.split(",");
    }

    public Collection<Account> getAccounts() {
        return accounts.values();
    }

    public void printSummary() {
        for (final Account a : accounts.values()) {
            a.printSummary();
        }
    }

    public List<Record> getAllRecords() {
        final List<Record> allRecords = new ArrayList<>();
        for (final Account a : accounts.values()) {
            allRecords.addAll(a.getRecords());
        }
        return allRecords;
    }

    public List<AggregatedRecord> groupByDescription(final String query) {
        String[] tags = parseArguments(query);
        final List<AggregatedRecord> tagRecords = new ArrayList<>();
        if (query.isEmpty())
            return tagRecords;

        if ((tags != null) && (tags.length > 0)) {

            for (Account a : accounts.values()) {
                a.setCovered(0);
            }

            for (String tag : tags) {
                tag = tag.trim();
                final AggregatedRecord tagRecord = new AggregatedRecord(tag, 0);
                for (Account a : accounts.values()) {
                    for (final Record r : a.getRecords()) {
                        if (StringUtils.containsIgnoreCase(r.getDescription(), tag)) {
                            tagRecord.addRecord(r);
                            r.setCovered(true);
                            a.setCovered(a.getCovered() + 1);
                        } else {
                            r.setCovered(false);
                        }
                    }
                }
                tagRecords.add(tagRecord);
            }
        }
        return tagRecords;
    }

    public Map<String,Double> getIncomePerItem(String query) {
        return getFieldPerRecord(query, r -> r.getPositive(), () -> getTotalIncome());
    }

    public Map<String, Double> getSpentPerItem(String query) {
        return getFieldPerRecord(query, r -> r.getNegative(), () -> getTotalSpent());
    }

    private Map<String,Double> getFieldPerRecord(
            String query,
            Function<AggregatedRecord, Double> func,
            Supplier<Double> totalFunc) {
        List<AggregatedRecord> groupData = groupByDescription(query);
        HashMap<String, Double> spent = new HashMap<>();
        double total = totalFunc.get();
        double leftTotal = total;
        for (AggregatedRecord record : groupData) {
            Double value = func.apply(record);
            spent.put(record.getDescription(),
                    (Math.abs(value) / total) * 100);
            leftTotal -= value;
        }
        spent.put("Other", leftTotal / total * 100);
        return spent;
    }


    public double getTotalIncome() {
        double income = 0;
        for (Account a : accounts.values())
            income += a.getRecords().stream().
                    mapToDouble(Record::getValue).
                    filter(x -> x > 0).
                    sum();
        return income;
    }

    public double getTotalSpent() {
        Double spent = 0.0;
        for (Account a : accounts.values()) {
            spent += a.getRecords().stream().
                    mapToDouble(Record::getValue).
                    filter(x -> x < 0).
                    map(x -> Math.abs(x)).
                    sum();
        }
        return spent;
    }

    public TimeSeries getWeeklyBalance() {
        return getBalance(new Week());
    }

    public TimeSeries getMonthlyBalance() {
        return getBalance(new Month());
    }

    public TimeSeries getWeeklyAveragesByDescription(String descriptionQuery) {
        List<Record> filtered = filterAny(descriptionQuery);
        return averageByPeriod(filtered, r -> r.getValue(), new Week());
    }

    public double getWeeklyAverageByDescription(String descriptionQuery) {
        List<Record> filtered = filterAny(descriptionQuery);
        TimeSeries ts = averageByPeriod(filtered, r -> r.getValue(), new Week());
        double rename = 0;
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem it = ts.getDataItem(i);
            rename += it.getValue().doubleValue();
        }
        return rename / ts.getItemCount();
    }

    private TimeSeries getBalance(RegularTimePeriod period) {
        return averageByPeriod(getAllRecords(), r -> r.getBalance(), period);
    }

    /**
     * Average records grouped in a certain unit of time (e.g. records per
     * Calendar.MONTH or Calendar.WEEK). This can be used for weekly/monthly
     * statistics
     */
    private TimeSeries averageByPeriod(List<Record> rs, Function<Record, Number> func, RegularTimePeriod period) {
        Map<RegularTimePeriod, Integer> periodCount = new HashMap<>();
        TimeSeries ts = new TimeSeries("T");
        for (Record r : rs) {
            // aggregate
            RegularTimePeriod timePeriod = getPeriodForDate(period, r.getDate().toDate());
            TimeSeriesDataItem tsItem = ts.getDataItem(timePeriod);
            double oldValue = tsItem == null ? 0 : tsItem.getValue().doubleValue();
            Integer count = periodCount.get(timePeriod);
            count = count == null ? 1 : count + 1;

            // XXX this leads to some (acceptable for now) loss of accuracy
            double newValue = (oldValue * (count - 1) + func.apply(r).doubleValue()) / count;
            ts.addOrUpdate(timePeriod, newValue);
            periodCount.put(timePeriod, count);
        }
        return ts;
    }

    private TimeSeries sumByPeriod(List<Record> rs, Function<Record, Number> func, RegularTimePeriod period) {
        TimeSeries ts = new TimeSeries("T");
        for (Record r : rs) {
            RegularTimePeriod timePeriod = getPeriodForDate(period, r.getDate().toDate());
            TimeSeriesDataItem tsItem = ts.getDataItem(timePeriod);
            double oldValue = tsItem == null ? 0 : tsItem.getValue().doubleValue();
            ts.addOrUpdate(timePeriod, oldValue + func.apply(r).doubleValue());
        }
        return ts;
    }

    private RegularTimePeriod getPeriodForDate(RegularTimePeriod period, Date d) {
        if (period instanceof  Month)
            return new Month(d);
        return new Week(d);
    }

    public TimeSeries getAveragesByDescription(String query, RegularTimePeriod period) {
        List<Record> filtered = filterAny(query);
        TimeSeries ts = averageByPeriod(filtered, r -> r.getValue(), period);
        return ts;
    }

    public TimeSeries getTotalByDescription(String query, RegularTimePeriod period) {
        List<Record> filtered = filterAny(query);
        TimeSeries ts = sumByPeriod(filtered, r -> r.getValue(), period);
        return ts;
    }
}
