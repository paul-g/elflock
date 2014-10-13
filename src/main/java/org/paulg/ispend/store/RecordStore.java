package org.paulg.ispend.store;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.paulg.ispend.model.Account;
import org.paulg.ispend.model.Record;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RecordStore {
    void addRecord(Record r);

    List<Record> getRecordsByAccountNumber(String number);

    List<Record> filterAll();

    Collection<Account> getAccounts();

    List<Record> getAllRecords();

    Map<String,Double> getIncomePerItem(String query);

    Map<String, Double> getSpentPerItem(String query);

    double getTotalIncome();

    double getTotalSpent();

    double getWeeklyAverageByDescription(String descriptionQuery);

    TimeSeries getWeeklyAveragesByDescription(String descriptionQuery);

    TimeSeries getAveragesByDescription(String query, RegularTimePeriod period);

    TimeSeries getTotalByDescription(String query, RegularTimePeriod period);

    TimeSeries getEndOfMonthBalance();
}
