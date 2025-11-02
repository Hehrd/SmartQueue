package com.alexander;

import com.alexander.model.Settings;
import com.alexander.model.Song;
import com.alexander.model.SongWithProperties;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import com.alexander.util.JsonUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.nio.file.Path;
import java.util.*;


@Component
public class SessionContext {
    @Getter
    private Settings settings;
    @Getter
    private User currentUser;
    @Getter
    private int totalSongs;
    private Map<String, SongWithProperties> addedSongs;

    private final FileUtil fileUtil;

    @Autowired
    public SessionContext(FileUtil fileUtil) {
        this.addedSongs = new HashMap<>();
        this.totalSongs = 0;
        this.fileUtil = fileUtil;
        this.currentUser = null;
        this.settings = null;
    }


    public void addSongs(Collection<? extends Song> songs) {
        for (Song song : songs) {
                addedSongs.putIfAbsent(song.getUri(), new SongWithProperties(song));
        }
        totalSongs = addedSongs.size();
    }

    public void addSongsWithProperties(Collection<SongWithProperties> songs) {
        for (SongWithProperties song : songs) {
            addedSongs.putIfAbsent(song.getUri(), song);
        }
        totalSongs = addedSongs.size();
    }

    public List<SongWithProperties> removeSongs(Collection<String> songUris) {
        List<SongWithProperties> removedSongs = new ArrayList<>();
        for (String uri : songUris) {
            SongWithProperties song = addedSongs.remove(uri);
            removedSongs.add(song);
        }
        totalSongs = addedSongs.size();
        return removedSongs;
    }

    public Map<String, SongWithProperties> getAddedSongsMap() {
        return addedSongs;
    }

    public List<SongWithProperties> getAddedSongsList() {
        return new ArrayList<>(addedSongs.values());
    }
    public SongWithProperties getAddedSong(String uri) {
        return addedSongs.get(uri);
    }

    public List<String> getSongUris() {
        return new ArrayList<>(addedSongs.keySet());
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        settings = loadSettings();
    }

    public String getCurrentUsernameAndId() {
        return String.format("%s-%s", currentUser.getDisplayName(), currentUser.getId());
    }

    public String getCurrentUserId() {
        return currentUser.getId();
    }

    private Settings loadSettings() {
        String currentUsernameAndId = getCurrentUsernameAndId();
        String settingsJson = fileUtil.readFromFile(
                Path.of(fileUtil.APP_DATA_SETTINGS.toString(), currentUsernameAndId + FileExtension.JSON.getValue()));
        Settings settings = JsonUtil.deserialize(settingsJson, Settings.class);
        return settings;
    }

}
