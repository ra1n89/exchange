package ru.exchange.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.exchange.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class ServletListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {

        // Initialize the database connection
        Connection connection = ConnectionManager.getConnection();

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
                "FOREIGN KEY (base_currency_id) REFERENCES currencies (id) ON DELETE CASCADE, " +
                "FOREIGN KEY (target_currency_id) REFERENCES currencies (id) ON DELETE CASCADE, " +
                "UNIQUE (base_currency_id, target_currency_id))";

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
