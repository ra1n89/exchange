package ru.exchange.web;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.exchange.dao.DbDao;
import ru.exchange.model.Currensy;

import java.io.IOException;
import java.io.PrintWriter;

public class Servlet extends HttpServlet {
    DbDao dbDao = new DbDao();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("hello");
        dbDao.createTable();
        dbDao.save(new Currensy("AUD", "Australian dollar", "A$"));
        dbDao.save(new Currensy("RUB", "Rubley", "RU"));
        System.out.println(dbDao.getById(2));
        //resp.sendRedirect("/json.jsp");
        String json = new ObjectMapper().writeValueAsString(dbDao.getById(2));
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);

    }
}
