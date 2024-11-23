package ru.exchange.dao;

import ru.exchange.model.ExchangeRate;
import ru.exchange.to.ExchangeRateTo;

import java.sql.SQLException;
import java.util.List;

public interface ExchangeDao {
    ExchangeRate save(ExchangeRate exchangeRate) throws SQLException;

    boolean update(ExchangeRate exchangeRate);

    boolean delete(int id);

    ExchangeRateTo getCurrencyByCode(String code) throws SQLException;

    List<ExchangeRate> getAll();

    boolean isExist(String baseCurrency, String targetCurrency);

}
