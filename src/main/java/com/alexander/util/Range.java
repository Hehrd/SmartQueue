package com.alexander.util;

import lombok.Data;

@Data
public class Range {
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    private int start;
    private int end;

    public boolean isNumberInRange(int number) {
        return number >= start && number <= end;
    }

    public boolean isBefore(int number) {
        return number < start;
    }

    public boolean isAfter(int number) {
        return number > end;
    }
}
