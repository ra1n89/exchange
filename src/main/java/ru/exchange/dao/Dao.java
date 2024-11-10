package ru.exchange.dao;

import ru.exchange.model.Currensy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface Dao {
    Currensy save(Currensy currensy) throws SQLException;
    boolean delete(int id);
    Currensy getCurrencyByCode(String code) throws SQLException;
    List<Currensy> getAll();
        void createTable();
}
