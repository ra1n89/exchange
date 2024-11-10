package ru.exchange.service;

import ru.exchange.dao.CurrencyDao;
import ru.exchange.dao.JdbcCurrencyCurrencyDao;
import ru.exchange.model.Currensy;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    CurrencyDao currencyDao = new JdbcCurrencyCurrencyDao();

    public List<Currensy> getAll() {
        return currencyDao.getAll();
    }

    public Currensy save(Currensy currency) throws SQLException {
        return currencyDao.save(currency);
    }

    public boolean delete(int id) {
        return currencyDao.delete(id);
    }

    public  void createTable() {
        currencyDao.createTable();
    }

    public Currensy getCurrencyByCode(String code) throws SQLException {
        return currencyDao.getCurrencyByCode(code);
    }

}
