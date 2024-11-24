package ru.exchange.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.service.ExchangeRateService;
import ru.exchange.to.ExchangeRateTo;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
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
}

