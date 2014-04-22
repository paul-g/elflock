package org.paulg.ispend.model;

import org.jfree.data.time.*;
import org.paulg.ispend.utils.StringUtils;

import java.util.*;
import java.util.function.Function;

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

    public List<Record> filter(final String text) {
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

    public Map<Date, Double> getWeeklyBalance() {
        return getBalance(Calendar.WEEK_OF_YEAR);
    }

    public Map<Date, Double> getMonthlyBalance() {
        return getBalance(Calendar.MONTH);
    }

    public Map<Date, Double> getWeeklyAveragesByDescription(String descriptionQuery) {
        List<Record> filtered = filter(descriptionQuery);
        TimeSeries ts = averageByPeriod(filtered, r -> r.getValue(), Calendar.WEEK_OF_YEAR);
        Map<Date, Double> newMap = new HashMap<>();
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem it = ts.getDataItem(i);
            newMap.put(it.getPeriod().getStart(), it.getValue().doubleValue());
        }
        return newMap;
    }

    public double getWeeklyAverageByDescription(String descriptionQuery) {
        List<Record> filtered = filter(descriptionQuery);
        TimeSeries ts = averageByPeriod(filtered, r -> r.getValue(), Calendar.WEEK_OF_YEAR);
        double rename = 0;
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem it = ts.getDataItem(i);
            rename += it.getValue().doubleValue();
        }
        return rename / ts.getItemCount();
    }

    private Map<Date, Double> getBalance(int period) {
        TimeSeries ts = averageByPeriod(getAllRecords(), r -> r.getBalance(), period);
        Map<Date, Double> newMap = new HashMap<>();
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem it = ts.getDataItem(i);
            newMap.put(it.getPeriod().getStart(), it.getValue().doubleValue());
        }
        return newMap;
    }

    /**
     * Average records grouped in a certain unit of time (e.g. records per
     * Calendar.MONTH or Calendar.WEEK). This can be used for weekly/monthly
     * statistics
     */
    private TimeSeries averageByPeriod(List<Record> rs, Function<Record, Number> func, int period) {
        Map<RegularTimePeriod, Integer> periodCount = new HashMap<>();
        TimeSeries ts = new TimeSeries("T");
        for (Record r : rs) {
            // aggregate
            RegularTimePeriod timePeriod;
            if (period == Calendar.MONTH)
                timePeriod = new Month(r.getDate().toDate());
            else
                timePeriod = new Week(r.getDate().toDate());
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
}
