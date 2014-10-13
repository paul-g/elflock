package org.paulg.ispend.model;

import org.jfree.data.time.*;
import org.joda.time.DateTime;
import org.paulg.ispend.utils.Pair;
import org.paulg.ispend.utils.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RecordStore {

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final Map<DateTime, MonthlyData> monthlyData = new LinkedHashMap<>();

    public void addRecord(final Record r) {
        Account acc = accounts.get(r.getAccountNumber());
        if (acc == null) {
            acc = new Account(r.getAccountNumber(), r.getAccountName());
            accounts.put(r.getAccountNumber(), acc);
        }
        acc.addRecord(r);

        // store it in the monthly data
        DateTime rd = r.getDate();
        DateTime dt = yearMonth(rd);

        MonthlyData md = monthlyData.get(dt);
        if (md == null) {
            md = new MonthlyData();
            monthlyData.put(dt, md);
        }
        md.addRecord(r, acc);
    }

    private DateTime yearMonth(DateTime rd) {
        return new DateTime().withYear(rd.getYear()).
                withMonthOfYear(rd.getMonthOfYear()).
                withDayOfMonth(1).withTime(0, 0, 0, 0);
    }

    public List<Record> getRecordsByAccountNumber(final String number) {
        if (accounts.get(number) != null) {
            return new ArrayList<>(accounts.get(number).getRecords());
        }
        return new ArrayList<>();
    }

    /** Returns the records matching any of the query atoms. Multiple query atoms
     *  are separated by comma. */
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

    private static List<String> splitQuery(String query) {
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

    private static List<Record> filterAtom(List<Record> records, final String text) {
        return records.stream()
                .filter(r -> StringUtils.containsIgnoreCase(r.getDescription(), text))
                .collect(Collectors.toList());
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

        if (tags != null && tags.length > 0) {

            for (Account a : accounts.values()) {
                a.setCovered(0);
            }

            for (final Record r : getAllRecords()) {
                r.setCovered(false);
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
                        }
                    }
                }
                tagRecords.add(tagRecord);
            }
        }
        return tagRecords;
    }

    public Map<String,Double> getIncomePerItem(String query) {
        return getFieldPerRecord(
                query,
                AggregatedRecord::getPositive,
                this::getTotalIncome);
    }

    public Map<String, Double> getSpentPerItem(String query) {
        return getFieldPerRecord(
                query,
                AggregatedRecord::getNegative,
                this::getTotalSpent);
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
                    map(Math::abs).
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
        List<Record> filtered = filterAny(getAllRecords(), descriptionQuery);
        return averageByPeriod(filtered, Record::getValue, new Week());
    }

    public double getWeeklyAverageByDescription(String descriptionQuery) {
        List<Record> filtered = filterAny(getAllRecords(), descriptionQuery);
        TimeSeries ts = averageByPeriod(filtered, Record::getValue, new Week());
        double rename = 0;
        for (int i = 0; i < ts.getItemCount(); i++) {
            TimeSeriesDataItem it = ts.getDataItem(i);
            rename += it.getValue().doubleValue();
        }
        return rename / ts.getItemCount();
    }

    private TimeSeries getBalance(RegularTimePeriod period) {
        return averageByPeriod(getAllRecords(), Record::getBalance, period);
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
        List<Record> filtered = filterAny(getAllRecords(), query);
        return averageByPeriod(filtered, r -> r.getValue(), period);
    }

    public TimeSeries getTotalByDescription(String query, RegularTimePeriod period) {
        List<Record> filtered = filterAny(getAllRecords(), query);
        return sumByPeriod(filtered, r -> r.getValue(), period);
    }

    public TimeSeries getEndOfMonthBalance() {
        TimeSeries ts = new TimeSeries("EomBalance");
        DateTime start = getFirstRecordDate();
        DateTime end = getLastRecordDate();
        while (start.isBefore(end)) {
            start = start.plusMonths(1);
            MonthlyData md = monthlyData.get(start);
            RegularTimePeriod tp = new Month(start.toDate());
            if (md != null) {
                Map<Account, Pair<Record, Record>> acmd = md.accMonthlyData;
                double aggregatedBalance = 0;
                for (Pair<Record, Record> me : acmd.values())
                    aggregatedBalance += me.snd.getBalance();
                ts.add(tp, aggregatedBalance);
            } else
                ts.add(tp, 0);
        }
        return ts;
    }

    private DateTime getFirstRecordDate() {
        return yearMonth(Collections.min(getAllRecords()).getDate());
    }

    private DateTime getLastRecordDate() {
        return yearMonth(Collections.max(getAllRecords()).getDate());
    }

    private static class MonthlyData {
        Map<Account, Pair<Record, Record>> accMonthlyData = new LinkedHashMap<>();

        public void addRecord(final Record r, final Account a) {
            Pair<Record, Record> amd = accMonthlyData.get(a);
            Record firstRecord = amd == null ? null : amd.fst;
            Record lastRecord = amd == null ? null : amd.snd;
            if (firstRecord == null || firstRecord.getDate().isAfter(r.getDate()))
                firstRecord = r;
            if (lastRecord == null || lastRecord.getDate().isBefore(r.getDate()))
                lastRecord = r;
            accMonthlyData.put(a, new Pair<>(firstRecord, lastRecord));
        }
    }
}
