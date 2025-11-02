package com.alexander.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.alexander.util.ParserUtil.parseBoolean;
import static com.alexander.util.ParserUtil.parseInt;

@AllArgsConstructor
@Getter
public class Settings {
    private int defaultQueueSize;
    private String defaultQueueName;
    private boolean allowRepeats;

    public Settings() {
        defaultQueueSize = 30;
        defaultQueueName = "queue";
        allowRepeats = true;
    }

    public void setProperty(String propertyName, String value) {
        switch (propertyName) {
            case "defaultQueueSize" -> this.defaultQueueSize = parseInt(value);
            case "defaultQueueName" -> this.defaultQueueName = value;
            case "allowRepeats" -> this.allowRepeats = parseBoolean(value);
            default -> throw new IllegalArgumentException("Unknown property: " + propertyName);
        }
    }

}
