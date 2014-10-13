package org.paulg.ispend.store;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.paulg.ispend.model.Account;
import org.paulg.ispend.model.AggregatedRecord;
import org.paulg.ispend.model.Record;
import org.paulg.ispend.utils.Pair;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface RecordStore {
    void addRecord(Record r);

    List<Record> getRecordsByAccountNumber(String number);

    List<Record> filterAll();

    Collection<Account> getAccounts();

    List<Record> getAllRecords();

    List<AggregatedRecord> groupByDescription(String query);

    Map<String,Double> getIncomePerItem(String query);

    Map<String, Double> getSpentPerItem(String query);

    double getTotalIncome();

    double getTotalSpent();

    double getWeeklyAverageByDescription(String descriptionQuery);

    TimeSeries getWeeklyBalance();

    TimeSeries getMonthlyBalance();

    TimeSeries getWeeklyAveragesByDescription(String descriptionQuery);

    TimeSeries getAveragesByDescription(String query, RegularTimePeriod period);

    TimeSeries getTotalByDescription(String query, RegularTimePeriod period);

    TimeSeries getEndOfMonthBalance();
}
