package ru.exchange.dao;

import ru.exchange.model.ExchangeRate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JdbcExchangeRateCurrencyDao implements ExchangeDao{

    Connection connection;

    public JdbcExchangeRateCurrencyDao() {
        // Initialize the database connection
        // Replace "sample.db" with your actual SQLite database file path
        try {
            Class.forName("org.sqlite.JDBC");  // Load SQLite JDBC driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/exchange.db";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) throws SQLException {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setDouble(3, exchangeRate.getRate());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }

        // надо бы разобраться как вернуть ID и засетить в сущность
        return exchangeRate;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public ExchangeRate getCurrencyByCode(String code) throws SQLException {
        return null;
    }

    @Override
    public List<ExchangeRate> getAll() {
        return null;
    }
}


