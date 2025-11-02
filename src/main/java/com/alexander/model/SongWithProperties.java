package com.alexander.model;

import com.alexander.model.Song;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.alexander.util.ParserUtil.parseInt;

@Getter
@Setter
public class SongWithProperties extends Song {
    private static final int DEFAULT_WEIGHT = 100;
    private static final int DEFAULT_MAX_REPEATS = 0;

    private int weight;
    private int maxRepeats;

    @JsonCreator
    public SongWithProperties(
            @JsonProperty("uri") String uri,
            @JsonProperty("name") String name,
            @JsonProperty("artists") String artists,
            @JsonProperty("weight") int weight,
            @JsonProperty("maxRepeats") int maxRepeats) {
        super(uri, name, artists);
        this.weight = weight;
        this.maxRepeats = maxRepeats;
    }

    public SongWithProperties(Song song) {
        super(song.getUri(), song.getName(), song.getArtists());
        this.weight = DEFAULT_WEIGHT;
        this.maxRepeats = DEFAULT_MAX_REPEATS;
    }

    public SongWithProperties(Song song, int weight, int maxRepeats) {
        super(song.getUri(), song.getName(), song.getArtists());
        this.weight = weight;
        this.maxRepeats = maxRepeats;
    }

    public void setProperty(String propertyName, String value) {
        switch (propertyName) {
            case "weight" -> this.weight = parseInt(value);
            case "maxRepeats" -> this.maxRepeats = parseInt(value);
            default -> throw new IllegalArgumentException("Unknown property: " + propertyName);
        }
    }


}
