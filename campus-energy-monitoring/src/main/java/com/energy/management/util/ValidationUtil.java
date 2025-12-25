package com.energy.management.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern SERIAL_NUMBER_PATTERN =
            Pattern.compile("^[A-Z0-9_]+$");

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidSerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.length() < 3) return false;
        return SERIAL_NUMBER_PATTERN.matcher(serialNumber).matches();
    }

    public static boolean isValidPowerValue(Double power) {
        return power != null && power >= 0 && power <= 100000; // 最大100kW
    }

    public static boolean isValidVoltageValue(Double voltage) {
        return voltage != null && voltage >= 0 && voltage <= 1000;
    }
}