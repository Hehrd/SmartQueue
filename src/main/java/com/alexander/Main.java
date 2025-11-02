package com.alexander;

import org.apache.hc.core5.http.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IOException, ParseException, SpotifyWebApiException {
//
//        SpotifyApi spotifyApi = SpotifyApi.builder()
//                .setClientSecret("b75d4419d24d417cb4f82354b7c50a2d")
//                .setClientId("d7dbf2d9f6fb4b3e944f14b3c74c4c96")
//                .setRedirectUri(URI.create("http://127.0.0.1:19000/smartqueue"))
//                .build();

//        URI uri = authorizationCodeUriRequest.execute();
//        System.out.println(uri);
//        PlaylistService playlistService = new PlaylistService(spotifyApi, spotifyApi.getCurrentUsersProfile().build().execute(), new WeightedRandomPicker<Song>());
//        playlistService.generateQueuePlaylist(queuePlaylist);
        SpringApplication.run(Main.class, args);
    }
}