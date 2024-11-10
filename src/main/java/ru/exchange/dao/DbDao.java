package ru.exchange.dao;

import ru.exchange.model.Currensy;

import java.sql.*;

public class DbDao implements Dao {
    // Implement methods for CRUD operations (create, read, update, delete) for entities
    Connection connection;

    public DbDao() {
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

    public Currensy save(Currensy currensy) {
        String sql = "INSERT INTO currencies (code, sign, full_name) VALUES (?, ?, ?)" ;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currensy.getCode());
            statement.setString(2, currensy.getSign());
            statement.setString(3, currensy.getFullName());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    public Currensy getById(int id) {
        String sql = "SELECT * FROM currencies WHERE id =?";
        Currensy currensy;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            String idFromTable = resultSet.getString(1);
            String code = resultSet.getString(2);
            String sign = resultSet.getString(3);
            String fullName = resultSet.getString(4);
            currensy = new Currensy(code, sign, fullName);
            currensy.setId(Integer.parseInt(idFromTable));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currensy;
    }



    public void createTable() {
        System.out.println(System.getProperty("user.dir"));
        String sqlDrop = "DROP TABLE IF EXISTS currencies ";
        String sqlCreate = "CREATE TABLE currencies (id INTEGER PRIMARY KEY, " +
                "code INTEGER, " +
                "sign VARCHAR," +
                "full_name VARCHAR)";

        try (Statement statement = connection.createStatement()) {
            System.out.println(statement.execute(sqlDrop));
            System.out.println(statement.execute(sqlCreate));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
