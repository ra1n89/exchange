package ru.exchange.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.dao.ExchangeDao;
import ru.exchange.dao.JdbcExchangeRateCurrencyDao;
import ru.exchange.model.Currensy;
import ru.exchange.model.ExchangeRate;
import ru.exchange.to.ExchangeRateTo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/exchangeRate/*")
public class ExchangeServlet extends HttpServlet {

    ExchangeDao exchangeDao = new JdbcExchangeRateCurrencyDao();


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if ((pathInfo == null) || (pathInfo.equals("/"))) {

            List<ExchangeRate> exchangeRate = exchangeDao.getAll();
            String exchangeRateJson = new ObjectMapper().writeValueAsString(exchangeRate);
            resp.getWriter().println(exchangeRateJson);
        } else {
            String code = pathInfo.substring(1);
            ExchangeRateTo exchangeRateTo = null;
            try {
                exchangeRateTo = exchangeDao.getCurrencyByCode(code);
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
}
