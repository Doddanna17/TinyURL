package com.doddanna.tinyUrl.utils;

public class Base62Converter {
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE62.length();
    private static final int FIXED_LENGTH = 8; // Desired length of the token

    public static String encode(int num) {
        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            sb.append(BASE62.charAt(num % BASE));
            num /= BASE;
        }

        String encoded = sb.reverse().toString();
        return padOrTrim(encoded, FIXED_LENGTH);
    }

    private static String padOrTrim(String token, int length) {
        if (token.length() == length) {
            return token;
        } else if (token.length() < length) {
            // Pad with leading zeros
            StringBuilder sb = new StringBuilder();
            while (sb.length() < length - token.length()) {
                sb.append('0');
            }
            sb.append(token);
            return sb.toString();
        } else {
            // This case is unlikely for base62 encoding with typical range sizes
            return token.substring(0, length);
        }
    }
}
