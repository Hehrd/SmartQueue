package com.alexander.auth.controller;

import com.alexander.auth.service.UserService;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @RequestMapping("/smartqueue")
    public ResponseEntity<String> getCode(@RequestParam("code") String code) throws IOException, ParseException, SpotifyWebApiException {
        userService.registerUser(code);
        return ResponseEntity.ok("OK");
    }


}
