package ru.exchange.dao;

import ru.exchange.model.Currensy;
import ru.exchange.model.ExchangeRate;
import ru.exchange.to.ExchangeRateTo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcExchangeRateCurrencyDao implements ExchangeDao {

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
    public boolean update(ExchangeRate exchangeRate) {
        String sql = "UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, exchangeRate.getRate());
            statement.setInt(2, exchangeRate.getBaseCurrencyId());
            statement.setInt(3, exchangeRate.getTargetCurrencyId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public ExchangeRateTo getCurrencyByCode(String code) throws SQLException {
        String baseCurrency = code.substring(0, 3);
        String targetCurrency = code.substring(3, 6);

        CurrencyDao currensyDao = new JdbcCurrencyCurrencyDao();
        Currensy baseCurrencyId = currensyDao.getCurrencyByCode(baseCurrency);
        Currensy targetCurrencyId = currensyDao.getCurrencyByCode(targetCurrency);
        //System.out.println(baseCurrency + "id=" + baseCurrencyId + " " + targetCurrency + " id=" + targetCurrencyId ) ;

        String sql = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";

        ExchangeRateTo exchangeRateTo;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, baseCurrencyId.getId());
            statement.setInt(2, targetCurrencyId.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int idFromTable = resultSet.getInt(1);
                int baseCurrencyIdFromTable = resultSet.getInt(2);
                int targetCurrencyIdFromTable = resultSet.getInt(3);
                double rateFromTable = resultSet.getDouble(4);
                exchangeRateTo = new ExchangeRateTo(baseCurrencyIdFromTable, targetCurrencyIdFromTable, List.of(baseCurrencyId, targetCurrencyId), rateFromTable);
                exchangeRateTo.setId(idFromTable);
            } else {
                throw new SQLException("Currency not found");
            }


        } catch (SQLException e) {
            throw e;
        }
        return exchangeRateTo;
    }

    @Override
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> rateList = new ArrayList<>();
        String sql = "SELECT * FROM exchange_rates";
        int id;
        int baseCurrencyId;
        int targetCurrencyId;
        Double rate;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                baseCurrencyId = resultSet.getInt("base_currency_id");
                targetCurrencyId = resultSet.getInt("target_currency_id");
                rate = resultSet.getDouble("rate");
                ExchangeRate exchangeRate = new ExchangeRate(baseCurrencyId, targetCurrencyId, rate);
                exchangeRate.setId(id);
                rateList.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rateList;
    }


}


