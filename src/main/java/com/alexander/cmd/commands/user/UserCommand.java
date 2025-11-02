package com.alexander.cmd.commands.user;


import com.alexander.SessionContext;
import com.alexander.auth.service.UserService;
import com.alexander.cmd.commands.Command;
import com.alexander.util.FileUtil;
import org.apache.hc.core5.http.ParseException;
import org.jasypt.encryption.StringEncryptor;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public abstract class UserCommand extends Command {
    protected final FileUtil fileUtil;
    protected final StringEncryptor stringEncryptor;
    protected final UserService userService;

    protected UserCommand(ComponentFlow.Builder componentFlowBuilder,
                          SessionContext sessionContext,
                          Terminal terminal,
                          FileUtil fileUtil,
                          UserService userService,
                          StringEncryptor stringEncryptor) {
        super(componentFlowBuilder, sessionContext, terminal);
        this.fileUtil = fileUtil;
        this.stringEncryptor = stringEncryptor;
        this.userService = userService;
    }

}
