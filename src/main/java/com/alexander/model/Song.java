package com.alexander.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Song {
    private String uri;
    private String name;
    private String artists;

    public Song(String uri, String name, String artists) {
        this.uri = uri;
        this.name = name;
        this.artists = artists;
    }

    @Override
    public String toString() {
        String str = String.format("%s - %s", name, artists);
        return str;
    }

}
