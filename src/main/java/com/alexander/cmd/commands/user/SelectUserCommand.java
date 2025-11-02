package com.alexander.cmd.commands.user;

import com.alexander.SessionContext;
import com.alexander.auth.service.UserService;
import com.alexander.cmd.commands.Command;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import org.apache.hc.core5.http.ParseException;
import org.jasypt.encryption.StringEncryptor;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ComponentFlow.ComponentFlowResult;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ShellComponent
public class SelectUserCommand extends UserCommand {
    protected static final String COMMAND_NAME = "selectUser";
    protected static final String COMMAND_DESC = "Select a user";


    @Autowired
    protected SelectUserCommand(ComponentFlow.Builder componentFlowBuilder,
                                SessionContext sessionContext,
                                Terminal terminal,
                                FileUtil fileUtil,
                                UserService userService,
                                StringEncryptor stringEncryptor) {
        super(componentFlowBuilder,
                sessionContext,
                terminal,
                fileUtil,
                userService,
                stringEncryptor);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        ComponentFlow componentFlow = getChooseUserFlow();
        ComponentFlowResult result = componentFlow.run();
        String usernameAndId = result.getContext().get("user-input");
        Path path = Path.of(fileUtil.APP_DATA_REFRESH_TOKENS.toString(),
                usernameAndId + FileExtension.TXT.getValue());
        String encryptedRefreshToken = fileUtil.readFromFile(path);
        String refreshToken = stringEncryptor.decrypt(encryptedRefreshToken);
        userService.changeCurrentUser(refreshToken);
        return getReturnString(usernameAndId);
    }

    private ComponentFlow getChooseUserFlow() throws IOException, ParseException, SpotifyWebApiException {
        Set<String> users = fileUtil.getFileNames(fileUtil.APP_DATA_REFRESH_TOKENS, FileExtension.TXT);
        List<SelectItem> items = new ArrayList<>();
        for (String user : users) {
            items.add(SelectItem.of(user, user));
        }
        return componentFlowBuilder.reset()
                .withSingleItemSelector("user-input")
                .name("Select a user: ")
                .selectItems(items)
                .next(allowCancel(null))
                .and()
                .build();
    }

    protected String getReturnString(String usernameAndId) {
        return String.format("Logged in as: %s", usernameAndId);
    }
}
