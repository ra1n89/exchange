package ru.exchange.utils;

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

    public static void validatePathExchangeRate(String pathInfo) throws MalformedURLException {
        if (pathInfo == null) {
            return;
        } else if (pathInfo.length() > 1) {
            throw new MalformedURLException("This URL is not exist");
        }
    }
}
