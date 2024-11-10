package ru.exchange.dao;

import ru.exchange.model.Currensy;

public interface Dao {
    Currensy save(Currensy currensy);
    boolean delete(int id);
    Currensy getById(int id);


}
