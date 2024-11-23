package ru.exchange.service;

import ru.exchange.dao.ExchangeDao;
import ru.exchange.dao.JdbcExchangeRateCurrencyDao;
import ru.exchange.model.ExchangeRate;
import ru.exchange.to.ExchangeRateTo;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {

    static private final ExchangeRateService EXCHANGE_RATE_SERVICE = new ExchangeRateService();
    ExchangeDao exchangeDao = JdbcExchangeRateCurrencyDao.getInstance();

    private ExchangeRateService() {
    }

    public ExchangeRate save(ExchangeRate exchangeRate) throws SQLException {

        return exchangeDao.save(exchangeRate);
    }

    public boolean update(ExchangeRate exchangeRate) {

        return exchangeDao.update(exchangeRate);
    }

    public boolean delete(int id) {
        return false;
    }

    public ExchangeRateTo getCurrencyByCode(String code) throws SQLException {

        return exchangeDao.getCurrencyByCode(code);
    }

    public List<ExchangeRate> getAll() {

        return exchangeDao.getAll();
    }

    public boolean isExist(String baseCurrency, String targetCurrency) {

        return exchangeDao.isExist(baseCurrency, targetCurrency);
    }

    public static ExchangeRateService getInstance() {
        return EXCHANGE_RATE_SERVICE;
    }
}


