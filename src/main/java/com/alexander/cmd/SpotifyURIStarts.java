package com.alexander.cmd;

public enum SpotifyURIStarts {
    SONG_URI_START("spotify:track:"),
    PLAYLIST_URI_START("spotify:playlist:"),
    ALBUM_URI_START("spotify:album:"),;

    private final String uriStart;

    SpotifyURIStarts(String uriStart) {
        this.uriStart = uriStart;
    }

    public String getUriStart() {
        return uriStart;
    }
}
