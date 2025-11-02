package com.alexander.cmd;

public enum SpotifyURLStarts {
    SONG_URL_START("https://open.spotify.com/track/"),
    PLAYLIST_URL_START("https://open.spotify.com/playlist/"),
    ALBUM_URL_START("https://open.spotify.com/album/");

    private final String urlStart;

    SpotifyURLStarts(String urlStart) {
        this.urlStart = urlStart;
    }

    public String getUrlStart() {
        return urlStart;
    }
}
