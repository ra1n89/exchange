package ru.exchange.web;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.dao.DbDao;
import ru.exchange.model.Currensy;
import ru.exchange.service.CurrencyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class Servlet extends HttpServlet {

    CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("hello");
        currencyService.createTable();
        try {
            currencyService.save(new Currensy("AUD", "Australian dollar", "A$"));
            currencyService.save(new Currensy("RUB", "Rubley", "RU"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //System.out.println(dbDao.getById(2));
        //resp.sendRedirect("/json.jsp");
       /* String json = new ObjectMapper().writeValueAsString(dbDao.get(2));
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);*/
    }
}
