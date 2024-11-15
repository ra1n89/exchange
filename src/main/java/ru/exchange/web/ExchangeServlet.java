package ru.exchange.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.dao.CurrencyDao;
import ru.exchange.dao.ExchangeDao;
import ru.exchange.dao.JdbcCurrencyDao;
import ru.exchange.dao.JdbcExchangeRateCurrencyDao;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    /*Обмен валюты #
GET /exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT #
Расчёт перевода определённого количества средств из одной валюты в другую. Пример запроса - GET /exchange?from=USD&to=AUD&amount=10.

Пример ответа:

Получение курса для обмена может пройти по одному из трёх сценариев. Допустим, совершаем перевод из валюты A в валюту B:

В таблице ExchangeRates существует валютная пара AB - берём её курс
В таблице ExchangeRates существует валютная пара BA - берем её курс, и считаем обратный, чтобы получить AB
В таблице ExchangeRates существуют валютные пары USD-A и USD-B - вычисляем из этих курсов курс AB
*/
    ExchangeDao exchangeDao = JdbcExchangeRateCurrencyDao.getInstance();
    CurrencyDao currencyDao = JdbcCurrencyDao.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Получение параметров из запроса
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if(from == null || to == null || amountStr == null || from.length() != 3 || to.length() != 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("{\"error\": \"Missing required fields\"}");
            return;
        }

        try {
            exchangeDao.isExist(from, to);
            currencyDao.getCurrencyByCode(to);

        } catch (SQLException e) {
            if (e.getMessage().contains("Currency not found")) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("{\"error\": \"Currency not found\"}");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

      /*  try {
            exchangeDao.getCurrencyByCode(from+to);
        } catch (SQLException e) {
            try {
                exchangeDao.getCurrencyByCode(to+from);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }*/


    }
}
