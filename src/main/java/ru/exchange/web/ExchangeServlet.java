package ru.exchange.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.model.Currensy;
import ru.exchange.service.CurrencyService;
import ru.exchange.service.ExchangeRateService;
import ru.exchange.to.ExchangeRateTo;
import ru.exchange.utils.ValidationUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Получение параметров из запроса
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        try {
            ValidationUtil.validateExchangeParameters(from, to, amountStr);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
            return;
        }

        try {
            //check if the currency pair exist in DB
            if (exchangeRateService.isExist(from, to)) {
                viewInResponse(resp, from, to, Integer.parseInt(amountStr), false, false);
                return;
            } else if (exchangeRateService.isExist(to, from)) {
                viewInResponse(resp, to, from, Integer.parseInt(amountStr), true, false);

            } else if (exchangeRateService.isExist("USD", from) && exchangeRateService.isExist("USD", to))
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
        Currensy currencyFrom = currencyService.getCurrencyByCode(baseCurrency);
        Currensy currencyTo = currencyService.getCurrencyByCode(targetCurrency);
        ExchangeRateTo currencyPairRate;
        if (isUsdDirection) {
            ExchangeRateTo exchangeRateUsdCurrencyFrom = exchangeRateService.getCurrencyByCode("USD" + currencyFrom.getCode());
            ExchangeRateTo exchangeRateUsdCurrencyTo = exchangeRateService.getCurrencyByCode("USD" + currencyTo.getCode());
            Double rateUsdCurrencyFrom = exchangeRateUsdCurrencyFrom.getRate();
            Double rateUsdCurrencyTo = exchangeRateUsdCurrencyTo.getRate();
            Double rateCurrencyFromCurrencyTo = rateUsdCurrencyFrom / rateUsdCurrencyTo;
            List<Currensy> list = new ArrayList<>();
            list.add(currencyFrom);
            list.add(currencyTo);
            currencyPairRate = new ExchangeRateTo(currencyFrom.getId(), currencyTo.getId(), list, rateCurrencyFromCurrencyTo);
        } else {

            currencyPairRate = exchangeRateService.getCurrencyByCode(baseCurrency + targetCurrency);
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

