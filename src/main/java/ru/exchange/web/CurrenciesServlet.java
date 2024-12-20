package ru.exchange.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.model.Currensy;
import ru.exchange.service.CurrencyService;
import ru.exchange.utils.ValidationUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/currencies/")
public class CurrenciesServlet extends HttpServlet {

    CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {

            List<Currensy> currensies = currencyService.getAll();

            String currensiesJson = new ObjectMapper().writeValueAsString(currensies);

            resp.getWriter().println(currensiesJson);
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
