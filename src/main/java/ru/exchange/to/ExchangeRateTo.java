package ru.exchange.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.exchange.model.Currensy;

import java.util.List;

public class ExchangeRateTo {

    private int id;
    @JsonIgnore
    private int baseCurrencyId;
    @JsonIgnore
    private int targetCurrencyId;

    private List<Currensy> currencies;
    private double rate;

    public ExchangeRateTo(int baseCurrencyId, int targetCurrencyId, List<Currensy> currencies, double rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.currencies = currencies;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(int targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public List<Currensy> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currensy> currencies) {
        this.currencies = currencies;
    }

    @Override
    public String toString() {
        return "ExchangeRateTo{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", currencies=" + currencies +
                ", rate=" + rate +
                '}';
    }
}


