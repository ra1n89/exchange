package ru.exchange.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.dao.CurrencyDao;
import ru.exchange.dao.ExchangeDao;
import ru.exchange.dao.JdbcCurrencyDao;
import ru.exchange.dao.JdbcExchangeRateCurrencyDao;
import ru.exchange.model.ExchangeRate;
import ru.exchange.service.CurrencyService;
import ru.exchange.service.ExchangeRateService;
import ru.exchange.to.ExchangeRateTo;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    CurrencyService currencyService = CurrencyService.getInstance();





    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if ((pathInfo == null) || (pathInfo.equals("/")) || (pathInfo.length() != 7)) {

            List<ExchangeRate> exchangeRate = exchangeRateService.getAll();
            String exchangeRateJson = new ObjectMapper().writeValueAsString(exchangeRate);
            resp.getWriter().println(exchangeRateJson);
        } else {
            String code = pathInfo.substring(1);
            ExchangeRateTo exchangeRateTo = null;
            try {
                exchangeRateTo = exchangeRateService.getCurrencyByCode(code);
            } catch (SQLException e) {
                System.out.println(e.getErrorCode() + e.getMessage());
                if (e.getMessage().contains("Currency not found")) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().println("{\"error\": \"Currency not found\"}");
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
            String exchangeRateToJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRateTo);
            resp.getWriter().println(exchangeRateToJson);
        }
        //resp.getWriter().println("Unsupported code: " + req.getParameter("code"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");
        if (baseCurrencyCode.isEmpty() || targetCurrencyCode.isEmpty() || rateString.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("{\"error\": \"Missing required fields\"}");
            return;
        }
        Double rate = Double.parseDouble(rateString);
        System.out.println("asd");

        try {
            int baseId = currencyService.getCurrencyByCode(baseCurrencyCode).getId();
            int targetId = currencyService.getCurrencyByCode(targetCurrencyCode).getId();
            exchangeRateService.save(new ExchangeRate(baseId, targetId, rate));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            //resp.setHeader("Location", req.getContextPath() +"/exchangeRate/" + baseCurrencyCode + targetCurrencyCode);
            resp.sendRedirect("/exchangeRate/" + baseCurrencyCode + targetCurrencyCode);
            //resp.setHeader("method", "GET");
            //req.getRequestDispatcher("/exchangeRate/" + baseCurrencyCode + targetCurrencyCode).forward(req, resp);
        } catch (SQLException e) {
            System.out.println(e.getErrorCode() + e.getMessage());
            if (e.getMessage().contains("Currency not found")) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("{\"error\": \"Currency not found\"}");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        String baseCurrency;
        String targetCurrency;
        String rateString;
        BufferedReader reader = req.getReader();

        if (pathInfo == null || pathInfo.length() != 7) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //
            return;
        }
        String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));

        Map<String, String> parameters = new HashMap<String, String>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] pairArray = pair.split("=");
            if (pairArray.length<2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            parameters.put(pairArray[0], pairArray[1]);
        }

        String code = pathInfo.substring(1);
        baseCurrency = code.substring(0, 3);
        targetCurrency = code.substring(3, 6);

        if (!parameters.containsKey("rate") || parameters.get("rate") == null || parameters.get("rate").isEmpty() || !isNumeric(parameters.get("rate"))) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //
            return;
        } else {
            rateString = parameters.get("rate");
        }

        try {
            int baseCurrencyId = currencyService.getCurrencyByCode(baseCurrency).getId();
            int targetCurrencyId = currencyService.getCurrencyByCode(targetCurrency).getId();
            exchangeRateService.update(new ExchangeRate(baseCurrencyId, targetCurrencyId, Double.parseDouble(rateString)));
        } catch (SQLException e) {
            System.out.println(e.getErrorCode() + e.getMessage());
            if (e.getMessage().contains("Currency not found")) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("{\"error\": \"Currency not found\"}");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isNumeric(String rate) {
        try {
            Double.parseDouble(rate);
        } catch (NumberFormatException e) {
            return false;

        }
        return true;
    }
}
