package org.paulg.ispend.store;

import org.jfree.data.time.*;
import org.joda.time.DateTime;
import org.paulg.ispend.model.Account;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.utils.Pair;

import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.paulg.ispend.store.Query.filterAny;
import static org.paulg.ispend.store.Query.splitQuery;

public class InMemoryRecordStore implements RecordStore {

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final Map<DateTime, MonthlyData> monthlyData = new LinkedHashMap<>();

    @Override
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

    @Override
    public List<Record> getRecordsByAccountNumber(final String number) {
        if (accounts.get(number) != null) {
            return new ArrayList<>(accounts.get(number).getRecords());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Record> filterAll() {
        throw new RuntimeException("RecordStore#filterAll() not implemented!");
    }

    @Override
    public Collection<Account> getAccounts() {
        return accounts.values();
    }

    @Override
    public List<Record> getAllRecords() {
        final List<Record> allRecords = new ArrayList<>();
        for (final Account a : accounts.values()) {
            allRecords.addAll(a.getRecords());
        }
        return allRecords;
    }

    @Override
    public Map<String,Double> getIncomePerItem(String query) {
        return getFieldPerRecord(
                query,
                x -> x > 0,
                this::getTotalIncome);
    }

    @Override
    public Map<String, Double> getSpentPerItem(String query) {
        return getFieldPerRecord(
                query,
                x -> x < 0,
                this::getTotalSpent);
    }

    private Map<String,Double> getFieldPerRecord(
            String query,
            DoublePredicate predicate,
            Supplier<Double> totalFunc) {
        Map<String, Double> spent = new HashMap<>();
        double totalSpent = totalFunc.get();
        double leftSpent = totalSpent;
        for (String t : splitQuery(query)) {
            double s = filterAny(getAllRecords(), query).stream().
                    mapToDouble(Record::getValue).
                    filter(predicate).
                    sum();
            spent.put(t, s / totalSpent * 100.0);
            leftSpent -= s;
        }
        spent.put("Other", leftSpent / totalSpent * 100.0);
        return spent;
    }


    @Override
    public double getTotalIncome() {
        return getAllRecords().stream().
                    mapToDouble(Record::getValue).
                    filter(x -> x > 0).
                    sum();
    }

    @Override
    public double getTotalSpent() {
        return getAllRecords().stream().
                mapToDouble(Record::getValue).
                filter(x -> x < 0).
                sum();
    }

    @Override
    public TimeSeries getWeeklyAveragesByDescription(String descriptionQuery) {
        List<Record> filtered = filterAny(getAllRecords(), descriptionQuery);
        return averageByPeriod(filtered, Record::getValue, new Week());
    }

    @Override
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

    @Override
    public TimeSeries getAveragesByDescription(String query, RegularTimePeriod period) {
        List<Record> filtered = filterAny(getAllRecords(), query);
        return averageByPeriod(filtered, Record::getValue, period);
    }

    @Override
    public TimeSeries getTotalByDescription(String query, RegularTimePeriod period) {
        List<Record> filtered = filterAny(getAllRecords(), query);
        return sumByPeriod(filtered, Record::getValue, period);
    }

    @Override
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

    @Override
    public TimeSeries getSpentPerMonth() {
        TimeSeries eom = getEndOfMonthBalance();
        TimeSeries ts = new TimeSeries("Spent Per Month");
        for (int i = 1; i < eom.getItemCount(); i++) {
            TimeSeriesDataItem eomItem = eom.getDataItem(i);
            TimeSeriesDataItem prevEomItem = eom.getDataItem(i - 1);
            Number value = eomItem.getValue().doubleValue() - prevEomItem.getValue().doubleValue();
            ts.add(new TimeSeriesDataItem(eomItem.getPeriod(), value));
        }
        return ts;
    }

    private DateTime getFirstRecordDate() {
        return yearMonth(Collections.min(getAllRecords()).getDate());
    }

    private DateTime getLastRecordDate() {
        return yearMonth(Collections.max(getAllRecords()).getDate());
    }

    public static class MonthlyData {
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
