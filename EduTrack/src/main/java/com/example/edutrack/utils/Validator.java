package com.example.edutrack.utils;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern CITY_STATE_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    public static boolean isFieldEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isNameValid(String name) {
        return !isFieldEmpty(name) && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isAgeValid(String ageStr) {
        if (isFieldEmpty(ageStr)) return false;
        try {
            int age = Integer.parseInt(ageStr);
            return age >= 5 && age <= 120;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCityOrStateValid(String text) {
        return !isFieldEmpty(text) && CITY_STATE_PATTERN.matcher(text).matches();
    }

    public static boolean isPhoneValid(String phone) {
        return !isFieldEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isEmailValid(String email) {
        return !isFieldEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isAcademicValueValid(String valueStr) {
        if (isFieldEmpty(valueStr)) return false;
        try {
            int value = Integer.parseInt(valueStr);
            return value >= 0 && value <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}