package ru.exchange.utils;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    //private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();

    private static HikariDataSource dataSource;

    static {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:C:/apache-tomcat-11.0.0/apache-tomcat-11.0.0/bin/exchange.db");
        dataSource.setDriverClassName("org.sqlite.JDBC");

    }

    private ConnectionManager() {

    }

    ;

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
