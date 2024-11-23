package ru.exchange.utils;

public class ValidationUtil {

    static public void validate(String code) throws IllegalArgumentException {
        if (code.length() != 3) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }

    public static void validate(String code, String sign, String fullName) {
        if (code == null || sign == null || fullName == null) {
            throw new IllegalArgumentException("Missing required fields");
        }

        if (code.length() != 3 || sign.length() != 1) {
            throw new IllegalArgumentException("Invalid code: " + code + " or sign:" + sign);
        }
    }
}
