package ru.exchange.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.net.MalformedURLException;

public class ValidationUtil {

    static public void validate(String code) throws IllegalArgumentException {
        if (code.length() != 3) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }

    public static void validate(String code, String sign, String fullName) throws IllegalArgumentException {
        if (code == null || sign == null || fullName == null) {
            throw new IllegalArgumentException("Missing required fields");
        }

        if (code.length() != 3 || sign.length() != 1) {
            throw new IllegalArgumentException("Invalid code: " + code + " or sign:" + sign);
        }
    }

    public static void validatePathExchangeRates(String pathInfo) throws MalformedURLException {
        if (pathInfo == null) {
            return;
        } else if (pathInfo.length() > 1) {
            throw new MalformedURLException("This URL is not exist");
        }
    }

    public static void validatePathExchangeRate(String pathInfo) {
        if (pathInfo.length() != 6) {
            throw new IllegalArgumentException("Invalid currencies: " + pathInfo);
        }
    }

    public static void validateExchangeParameters(String from, String to, String amountStr) {
        if (from == null || to == null || amountStr == null) {
            throw new IllegalArgumentException("All arguments must be provided ");
        }

        if (from.isEmpty() || to.isEmpty() || amountStr.isEmpty()) {
            throw new IllegalArgumentException("All arguments must be provided ");
        }

        if (from.length() != 3 || to.length() != 3 || !isNumeric(amountStr)) {
            throw new IllegalArgumentException("Invalid arguments provided ");
        }
    }

    private static boolean isNumeric(String numberAsString){
        try {
            Double.parseDouble(numberAsString);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
