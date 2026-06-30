package com.restosync.comandas.util;

public final class TextNormalizer {

    private TextNormalizer() {
    }

    public static String required(String value) {
        return nullable(value);
    }

    public static String nullable(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }

    public static String email(String value) {
        String normalized = nullable(value);
        return normalized == null ? null : normalized.toLowerCase();
    }
}
