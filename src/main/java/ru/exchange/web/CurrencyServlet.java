package ru.exchange.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ru.exchange.model.Currensy;
import ru.exchange.service.CurrencyService;
import ru.exchange.service.ExchangeRateService;
import ru.exchange.utils.ValidationUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/currencies/*")
public class CurrencyServlet extends HttpServlet {

    CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        String pathInfo = req.getPathInfo();



        if ((pathInfo == null) || (pathInfo.equals("/"))) {

            List<Currensy> currensies = currencyService.getAll();

            String currensiesJson = new ObjectMapper().writeValueAsString(currensies);

            resp.getWriter().println(currensiesJson);

        } else {
            String code = pathInfo.substring(1);
            try {
                ValidationUtil.validate(code);
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(e.getMessage());
                return;
            }
            Currensy currens = null;
            try {
                currens = currencyService.getCurrencyByCode(code);
            } catch (SQLException e) {
                System.out.println(e.getErrorCode() + e.getMessage());
                if (e.getMessage().contains("Currency not found")) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().println("{\"error\": \"Currency not found\"}");
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            String currensJson = new ObjectMapper().writeValueAsString(currens);
            resp.getWriter().println(currensJson);
        }
        // resp.getWriter().println("Unsupported code: " + req.getParameter("code"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        String fullName = req.getParameter("fullName");

        try {
            ValidationUtil.validate(code, sign, fullName);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
            return;
        }


        Currensy currensy = new Currensy(code, sign, fullName);
        try {
            String json = new ObjectMapper().writeValueAsString(currencyService.save(currensy));
            resp.getWriter().println(json);
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println("{\"error\": \"Currensy already exists\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println("{\"error\": \"Database error\"}");
                throw new RuntimeException();
            }
        }

    }
}
