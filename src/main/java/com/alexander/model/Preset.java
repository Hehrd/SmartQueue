package com.alexander.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Preset {
    private String name;
    private int totalSongs;
    private Map<String, SongWithProperties> songs;

    public List<SongWithProperties> getSongsList() {
        return new ArrayList<>(songs.values());
    }

    public SongWithProperties getSong(String songUri) {
        return songs.get(songUri);
    }
}
