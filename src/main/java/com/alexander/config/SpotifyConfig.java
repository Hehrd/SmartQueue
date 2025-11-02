package com.alexander.config;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.net.URI;

@Configuration
public class SpotifyConfig {
    @Bean
    public SpotifyApi spotifyApi(
            @Value("${spotify.client.secret}") String clientSecret,
            @Value("${spotify.client.id}") String clientId,
            @Value("${spotify.redirect-uri}") String redirectUri) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = SpotifyApi.builder()
                .setClientSecret(clientSecret)
                .setClientId(clientId)
                .setRedirectUri(URI.create(redirectUri))
                .build();
        return spotifyApi;
    }



}
