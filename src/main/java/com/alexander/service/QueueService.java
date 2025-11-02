package com.alexander.service;

import com.alexander.SessionContext;
import com.alexander.cmd.SpotifyURIStarts;
import com.alexander.cmd.SpotifyURLStarts;
import com.alexander.model.Song;
import com.alexander.util.TransformerUtil;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueueService {
    private final SpotifyApi spotifyApi;
    private final TransformerUtil transformerUtil;
    private final SessionContext sessionContext;

    private final int SPOTIFY_PAGE_SIZE_LIMIT = 100;
    private final int SPOTIFY_PLAYLIST_SIZE_LIMIT = 10000;

    @Autowired
    public QueueService(SpotifyApi spotifyApi,
                        TransformerUtil transformerUtil,
                        SessionContext sessionContext) {
        this.spotifyApi = spotifyApi;
        this.transformerUtil = transformerUtil;
        this.sessionContext = sessionContext;
    }


    public List<Song> getSongs(String url) throws IOException, ParseException, SpotifyWebApiException {
        String id = extractId(url);
        if (url.startsWith(SpotifyURLStarts.SONG_URL_START.getUrlStart())) {
            return getSong(id);
        } else if (url.startsWith(SpotifyURLStarts.PLAYLIST_URL_START.getUrlStart())) {
            return getSongsFromPlaylist(id);
        } else if (url.startsWith(SpotifyURLStarts.ALBUM_URL_START.getUrlStart())) {
            return getSongsFromAlbum(id);
        }
        return new ArrayList();
    }

    public String createPlaylist(String name) throws IOException, ParseException, SpotifyWebApiException {
        String userId = sessionContext.getCurrentUserId();
        CreatePlaylistRequest req = spotifyApi.createPlaylist(userId, name).build();
        Playlist playlist = req.execute();
        String playlistId = playlist.getId();
        return playlistId;
    }

    public void addSongsToPlaylist(String playlistId, List<String> songsUris) throws IOException, ParseException, SpotifyWebApiException {
        AddItemsToPlaylistRequest req = spotifyApi.addItemsToPlaylist(playlistId, songsUris.toArray(new String[0])).build();
        req.execute();
    }

    private List<Song> getSong(String id) throws IOException, ParseException, SpotifyWebApiException {
        GetTrackRequest req = spotifyApi.getTrack(id).build();
        Track track = req.execute();
        List<Song> songsList = new ArrayList<>();
        songsList.add(transformerUtil.toSong(track));
        return songsList;
    }

    private List<Song> getSongsFromPlaylist(String id) throws IOException, ParseException, SpotifyWebApiException {
        List<Song> songsList = new ArrayList<>();
        int offset = 0;
        while (true) {
            Paging<PlaylistTrack> currPage = getTracksPageFromPlaylist(id, offset);
            if (currPage == null || currPage.getItems().length == 0) {
                break;
            }
            PlaylistTrack[] playlistTracks = currPage.getItems();
            List<Song> songsFromPage = transformerUtil.toSongsList(playlistTracks);
            songsList.addAll(songsFromPage);
            offset += SPOTIFY_PAGE_SIZE_LIMIT;
        }
        return songsList;
    }

    private List<Song> getSongsFromAlbum(String id) throws IOException, ParseException, SpotifyWebApiException {
        List<Song> songsList = new ArrayList<>();
        int offset = 0;
        while (true) {
            Paging<TrackSimplified> currPage = getTracksPageFromAlbum(id, offset);
            if (currPage == null || currPage.getItems().length == 0) {
                break;
            }
            TrackSimplified[] simplifiedTracks = currPage.getItems();
            List<Song> songsFromPage = transformerUtil.toSongsList(simplifiedTracks);
            songsList.addAll(songsFromPage);
            offset += SPOTIFY_PAGE_SIZE_LIMIT;
        }
        return songsList;
    }


    private Paging<PlaylistTrack> getTracksPageFromPlaylist(String playlistId, int offset) throws IOException, ParseException, SpotifyWebApiException {
        GetPlaylistsItemsRequest req = spotifyApi.getPlaylistsItems(playlistId)
                .offset(offset)
                .limit(SPOTIFY_PAGE_SIZE_LIMIT)
                .build();
        Paging<PlaylistTrack> tracksPage = req.execute();
        return tracksPage;
    }

    private Paging<TrackSimplified> getTracksPageFromAlbum(String albumId, int offset) throws IOException, ParseException, SpotifyWebApiException {
        GetAlbumsTracksRequest req = spotifyApi.getAlbumsTracks(albumId)
                .offset(offset)
                .limit(SPOTIFY_PAGE_SIZE_LIMIT)
                .build();
        Paging<TrackSimplified> tracksPage = req.execute();
        TrackSimplified tracks[] = tracksPage.getItems();

        return tracksPage;
    }

    private String getUri(String url, SpotifyURLStarts urlStart, SpotifyURIStarts uriStart) {
        String uri = url.replace(urlStart.getUrlStart(), uriStart.getUriStart());
        String uriWithoutQueryParams = uri.split("\\?")[0];
        return uriWithoutQueryParams;
    }

    public static String extractId(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        int lastSlash = url.lastIndexOf('/');
        int questionMark = url.indexOf('?', lastSlash);
        int start = (lastSlash >= 0) ? lastSlash + 1 : 0;
        int end = (questionMark >= 0) ? questionMark : url.length();
        if (start > end) {
            return "";
        }
        return url.substring(start, end);
    }


}
