package ru.exchange.dao;

import ru.exchange.model.Currensy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCurrencyCurrencyDao implements CurrencyDao {
    // Implement methods for CRUD operations (create, read, update, delete) for entities
    Connection connection;

    public JdbcCurrencyCurrencyDao() {
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

    public Currensy save(Currensy currensy) throws SQLException {
        String sql = "INSERT INTO currencies (code, sign, full_name) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currensy.getCode());
            statement.setString(2, currensy.getSign());
            statement.setString(3, currensy.getFullName());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }


        return currensy;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM currencies WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public Currensy getCurrencyByCode(String code) throws SQLException {
        String sql = "SELECT * FROM currencies WHERE code =?";
        Currensy currensy;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String idFromTable = resultSet.getString(1);
                String codeFromTable = resultSet.getString(2);
                String sign = resultSet.getString(3);
                String fullName = resultSet.getString(4);
                currensy = new Currensy(codeFromTable, sign, fullName);
                currensy.setId(Integer.parseInt(idFromTable));
            } else {
                throw new SQLException("Currency not found");
            }


        } catch (SQLException e) {
            throw e;
        }
        return currensy;
    }

    @Override
    public List<Currensy> getAll() {

        List<Currensy> currensyList = new ArrayList<Currensy>();
        String sql = "SELECT * FROM currencies";
        int id;
        String code;
        String sign;
        String fullName;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                code = resultSet.getString("code");
                sign = resultSet.getString("sign");
                fullName = resultSet.getString("full_name");
                Currensy currensy = new Currensy(code, sign, fullName);
                currensy.setId(id);
                currensyList.add(currensy);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currensyList;
    }


    public void createTable() {
        System.out.println(System.getProperty("user.dir"));
        String sqlDropСurrencyTable = "DROP TABLE IF EXISTS currencies";
        String sqlCreateCurrencyTable = "CREATE TABLE currencies (id INTEGER PRIMARY KEY, " +
                "code INTEGER UNIQUE NOT NULL , " +
                "sign VARCHAR NOT NULL ," +
                "full_name VARCHAR NOT NULL)";


        String sqlDropExcangeRatesTable = "DROP TABLE IF EXISTS exchange_rates";
        String sqlCreateExcangeRatesTable = "CREATE TABLE exchange_rates (id INTEGER PRIMARY KEY, " +
                "base_currency_id INTEGER, " +
                "target_currency_id INTEGER," +
                "rate REAL," +
                "FOREIGN KEY (base_currency_id) REFERENCES currencies (id) ON DELETE CASCADE," +
                "FOREIGN KEY (target_currency_id) REFERENCES currencies (id) ON DELETE CASCADE)";


        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlDropExcangeRatesTable);
            statement.execute(sqlDropСurrencyTable);
            statement.execute(sqlCreateCurrencyTable);
            statement.execute(sqlCreateExcangeRatesTable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
