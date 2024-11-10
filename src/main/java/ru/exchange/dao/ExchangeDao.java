package ru.exchange.dao;

import ru.exchange.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public interface ExchangeDao {
    ExchangeRate save(ExchangeRate exchangeRate) throws SQLException;
    boolean delete(int id);
    ExchangeRate getCurrencyByCode(String code) throws SQLException;
    List<ExchangeRate> getAll();

}
