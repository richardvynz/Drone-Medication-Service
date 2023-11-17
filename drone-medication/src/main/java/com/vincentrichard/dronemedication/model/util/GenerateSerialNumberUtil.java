package com.vincentrichard.dronemedication.model.util;

import org.springframework.stereotype.Component;

public class GenerateSerialNumberUtil {
    private static final int SERIAL_NUMBER_LENGTH = 15;

    public static String generateSerialNumber() {
        StringBuilder serialNumber = new StringBuilder();

        for (int i = 0; i < SERIAL_NUMBER_LENGTH; i++) {
            char randomChar = generateRandomChar();
            serialNumber.append(randomChar);
        }

        return serialNumber.toString();
    }

    private static char generateRandomChar() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int randomIndex = (int) (Math.random() * characters.length());
        return characters.charAt(randomIndex);
    }
}
