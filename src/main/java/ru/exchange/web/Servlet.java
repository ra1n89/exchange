package ru.exchange.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.dao.ExchangeDao;
import ru.exchange.dao.JdbcExchangeRateCurrencyDao;
import ru.exchange.model.Currensy;
import ru.exchange.model.ExchangeRate;
import ru.exchange.service.CurrencyService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/hello")
public class Servlet extends HttpServlet {

    CurrencyService currencyService = CurrencyService.getInstance();
    ExchangeDao exchangeDao = JdbcExchangeRateCurrencyDao.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        currencyService.createTable();

        try {
            currencyService.save(new Currensy("AUD", "A$", "Australian dollar"));
            currencyService.save(new Currensy("USD", "$", "American dollar"));
            currencyService.save(new Currensy("RUB", "₽", "Ruble"));
            exchangeDao.save(new ExchangeRate(1, 2, new BigDecimal("0.010221")));
            exchangeDao.save(new ExchangeRate(1, 3, new BigDecimal("0.020221")));
            exchangeDao.save(new ExchangeRate(2, 3, new BigDecimal("0.98")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
