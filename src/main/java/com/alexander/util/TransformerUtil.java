package com.alexander.util;

import com.alexander.model.Song;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransformerUtil {

    public Song toSong(Track track) {
        if (track == null) return null;

        String uri = track.getUri();
        String name = track.getName();
        String artists = getArtistsNames(track.getArtists());

        return new Song(uri, name, artists);
    }

    public List<Song> toSongsList(PlaylistTrack[] playlistTracks) {
        List<Song> songs = new ArrayList<>();
        if (playlistTracks == null) return songs;

        for (PlaylistTrack pt : playlistTracks) {
            if (pt.getTrack() instanceof Track) {
                songs.add(toSong((Track) pt.getTrack()));
            }
        }
        return songs;
    }

    public List<Song> toSongsList(TrackSimplified[] simplifiedTracks) {
        List<Song> songs = new ArrayList<>();
        if (simplifiedTracks == null) return songs;

        for (TrackSimplified ts : simplifiedTracks) {
            String uri = ts.getUri();
            String name = ts.getName();
            String artists = getArtistsNames(ts.getArtists());
            songs.add(new Song(uri, name, artists));
        }
        return songs;
    }

    private String getArtistsNames(ArtistSimplified[] artists) {
        if (artists == null || artists.length == 0) return "";

        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (ArtistSimplified artist : artists) {
            builder.append(artist.getName());
            count++;
            if (count < artists.length) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
