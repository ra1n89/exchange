package ru.exchange.service;

import ru.exchange.dao.Dao;
import ru.exchange.dao.DbDao;
import ru.exchange.model.Currensy;

import java.sql.SQLException;
import java.util.Currency;
import java.util.List;

public class CurrencyService {
    Dao dao = new DbDao();

    public List<Currensy> getAll() {
        return dao.getAll();
    }

    public Currensy save(Currensy currency) throws SQLException {
        return dao.save(currency);
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }

    public  void createTable() {
        dao.createTable();
    }

    public Currensy getCurrencyByCode(String code) throws SQLException {
        return dao.getCurrencyByCode(code);
    }

}
