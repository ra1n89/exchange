package ru.exchange.utils;

import jakarta.servlet.http.HttpServletResponse;

public class ValidationUtil {

    static public void validate(String code) throws IllegalArgumentException{
        if (code.length() != 3) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }
}
