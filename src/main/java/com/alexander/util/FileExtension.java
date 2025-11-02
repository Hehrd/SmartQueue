package com.alexander.util;

public enum FileExtension {
    TXT (".txt"),
    JSON(".json");

    private final String value;

    public String getValue() {
        return value;
    }

    FileExtension(String value) {
        this.value = value;
    }
}
