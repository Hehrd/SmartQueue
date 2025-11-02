package com.alexander.auth.service;

import com.alexander.SessionContext;
import com.alexander.model.Settings;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import com.alexander.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import org.apache.hc.core5.http.ParseException;
import org.jasypt.encryption.StringEncryptor;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AuthorizationScope;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Set;

@Service
public class UserService {
    private static final int ACCESS_TOKEN_REFRESH_PERIOD_MILLIS = 50 * 60 * 1000 ;

    private final FileUtil fileUtil;
    private final JsonUtil jsonUtil;
    private final StringEncryptor stringEncryptor;
    private final Terminal terminal;
    private final SpotifyApi spotifyApi;
    private final SessionContext sessionContext;

    @Autowired
    public UserService(SpotifyApi spotifyApi,
                       FileUtil fileUtil,
                       Terminal terminal,
                       JsonUtil jsonUtil,
                       SessionContext sessionContext,
                       StringEncryptor stringEncryptor) {
        this.spotifyApi = spotifyApi;
        this.fileUtil = fileUtil;
        this.jsonUtil = jsonUtil;
        this.sessionContext = sessionContext;
        this.stringEncryptor = stringEncryptor;
        this.terminal = terminal;
    }

    @PostConstruct
    public void checkUsers() throws IOException, SpotifyWebApiException, ParseException {
        Set<String> users = fileUtil.getFileNames(Path.of(fileUtil.APP_DATA_REFRESH_TOKENS.toString()), FileExtension.TXT);
        URI uri = getRedirectUri();
        if (users == null || users.isEmpty()) {
            terminal.writer().println("No users found");
            terminal.writer().println(String.format("Register a user on this url: %s", uri));
            terminal.writer().flush();
            return;
        }
        selectDefaultUser();
    }

    @Scheduled(initialDelay = ACCESS_TOKEN_REFRESH_PERIOD_MILLIS,
            fixedRate = ACCESS_TOKEN_REFRESH_PERIOD_MILLIS)
    public void refreshApiClient() throws IOException, ParseException, SpotifyWebApiException {
        AuthorizationCodeRefreshRequest refreshRequest = spotifyApi
                .authorizationCodeRefresh()
                .build();
        AuthorizationCodeCredentials credentials = refreshRequest.execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
    }


    public void registerUser(String code) throws IOException, ParseException, SpotifyWebApiException {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();
        String accessToken = credentials.getAccessToken();
        String refreshToken = credentials.getRefreshToken();
        String encryptedRefreshToken = stringEncryptor.encrypt(refreshToken);
        String usernameAndId = getUsernameAndId(accessToken);
        Path refreshTokenPath = Path.of(fileUtil.APP_DATA_REFRESH_TOKENS.toString(),
                usernameAndId + FileExtension.TXT.getValue());
        Path settingsPath = Path.of(fileUtil.APP_DATA_SETTINGS.toString(),
                usernameAndId + FileExtension.JSON.getValue());
        fileUtil.writeInFile(refreshTokenPath, encryptedRefreshToken, true);
        fileUtil.writeInFile(settingsPath, jsonUtil.serialize(new Settings()), true);
    }

    public void changeCurrentUser(String refreshToken) throws IOException, ParseException, SpotifyWebApiException {
        spotifyApi.setRefreshToken(refreshToken);
        refreshApiClient();
        sessionContext.setCurrentUser(getUserFromApi());
    }

    private User getUserFromApi() throws IOException, ParseException, SpotifyWebApiException {
        GetCurrentUsersProfileRequest req = spotifyApi.getCurrentUsersProfile().build();
        User user = req.execute();
        return user;
    }

    private String getUsernameAndId(String accessToken) throws IOException, ParseException, SpotifyWebApiException {
        spotifyApi.setAccessToken(accessToken);
        GetCurrentUsersProfileRequest profileRequest =
                spotifyApi.getCurrentUsersProfile().build();

        User userProfile = profileRequest.execute();
        String userId = userProfile.getId();
        String displayName = userProfile.getDisplayName();
        return String.format("%s-%s", displayName, userId);
    }

    public URI getRedirectUri() throws IOException, SpotifyWebApiException {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(AuthorizationScope.USER_MODIFY_PLAYBACK_STATE,
                        AuthorizationScope.USER_READ_PLAYBACK_STATE,
                        AuthorizationScope.PLAYLIST_MODIFY_PRIVATE,
                        AuthorizationScope.PLAYLIST_MODIFY_PUBLIC)
                .build();
        return authorizationCodeUriRequest.execute();
    }

    private void selectDefaultUser() throws IOException, ParseException, SpotifyWebApiException {
        String usernameAndId = fileUtil.readFromFile(Path.of(fileUtil.APP_DATA_REFRESH_TOKENS.toString(),
                "default" + FileExtension.TXT.getValue()));
        Set<String> users = fileUtil.getFileNames(fileUtil.APP_DATA_REFRESH_TOKENS, FileExtension.TXT);
        if (users.size() == 1 && usernameAndId.isBlank()) {
            usernameAndId = users.iterator().next();
        }
        String encryptedRefreshToken = fileUtil.readFromFile(
                Path.of(fileUtil.APP_DATA_REFRESH_TOKENS.toString(),
                        usernameAndId + FileExtension.TXT.getValue()));
        String refreshToken = stringEncryptor.decrypt(encryptedRefreshToken);
        changeCurrentUser(refreshToken);
    }

}
