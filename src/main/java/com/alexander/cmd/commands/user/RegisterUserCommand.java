package com.alexander.cmd.commands.user;

import com.alexander.SessionContext;
import com.alexander.auth.service.UserService;
import com.alexander.util.FileUtil;
import org.apache.hc.core5.http.ParseException;
import org.jasypt.encryption.StringEncryptor;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@ShellComponent
public class RegisterUserCommand extends UserCommand{
    protected static final String COMMAND_NAME = "genRegistrationLink";
    protected static final String COMMAND_DESC = "Generate a registration link";

    protected RegisterUserCommand(ComponentFlow.Builder componentFlowBuilder,
                                  SessionContext sessionContext,
                                  Terminal terminal,
                                  FileUtil fileUtil,
                                  UserService userService,
                                  StringEncryptor stringEncryptor) {
        super(componentFlowBuilder, sessionContext, terminal, fileUtil, userService, stringEncryptor);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        return String.format("Register on: %s", userService.getRedirectUri());
    }
}
