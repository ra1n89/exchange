package ru.exchange.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.dao.CurrencyDao;
import ru.exchange.dao.ExchangeDao;
import ru.exchange.dao.JdbcCurrencyDao;
import ru.exchange.dao.JdbcExchangeRateCurrencyDao;
import ru.exchange.model.Currensy;
import ru.exchange.to.ExchangeRateTo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

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

        if (from == null || to == null || amountStr == null || from.length() != 3 || to.length() != 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("{\"error\": \"Missing required fields\"}");
            return;
        }

        try {
            //check if the currency pair exist in DB
            if (exchangeDao.isExist(from, to)) {
                viewInResponse(resp, from, to, Integer.parseInt(amountStr), false, false);
                return;
            } else if (exchangeDao.isExist(to, from)) {
                viewInResponse(resp, to, from, Integer.parseInt(amountStr), true, false);

            } else if (exchangeDao.isExist("USD", from) && exchangeDao.isExist("USD", to))
               viewInResponse(resp, from, to, Integer.parseInt(amountStr), false, true);
        } catch (SQLException e) {
            if (e.getMessage().contains("Currency not found")) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("{\"error\": \"Currency not found\"}");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    public void viewInResponse(HttpServletResponse resp, String baseCurrency, String targetCurrency, int amount, boolean isRevertedDirection, boolean isUsdDirection) throws SQLException, IOException {
        Currensy currencyFrom = currencyDao.getCurrencyByCode(baseCurrency);
        Currensy currencyTo = currencyDao.getCurrencyByCode(targetCurrency);
        ExchangeRateTo currencyPairRate;
        if (isUsdDirection) {
            ExchangeRateTo exchangeRateUsdCurrencyFrom = exchangeDao.getCurrencyByCode("USD" + currencyFrom.getCode());
            ExchangeRateTo exchangeRateUsdCurrencyTo = exchangeDao.getCurrencyByCode("USD" + currencyTo.getCode());
            Double rateUsdCurrencyFrom = exchangeRateUsdCurrencyFrom.getRate();
            Double rateUsdCurrencyTo = exchangeRateUsdCurrencyTo.getRate();
            Double rateCurrencyFromCurrencyTo = rateUsdCurrencyFrom / rateUsdCurrencyTo;
            List<Currensy> list = new ArrayList<>();
            list.add(currencyFrom);
            list.add(currencyTo);
            currencyPairRate = new ExchangeRateTo(currencyFrom.getId(), currencyTo.getId(), list, rateCurrencyFromCurrencyTo);
        } else {

            currencyPairRate = exchangeDao.getCurrencyByCode(baseCurrency + targetCurrency);
            if (isRevertedDirection) {
                currencyPairRate.setRate(1 / currencyPairRate.getRate());
            }
        }

            ObjectMapper mapper = new ObjectMapper();
            mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());

            JsonNode jsonNode = mapper.valueToTree(currencyPairRate);
            ObjectNode combinedNode = mapper.createObjectNode();
            combinedNode.set("currencyPairRate", jsonNode);
            combinedNode.put("amount", amount);
            combinedNode.put("convertedAmount", amount * currencyPairRate.getRate());

            String exchangeRateToJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(combinedNode);
            resp.getWriter().println(exchangeRateToJson);
        }
    }

