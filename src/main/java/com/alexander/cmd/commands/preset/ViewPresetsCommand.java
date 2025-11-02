package com.alexander.cmd.commands.preset;

import com.alexander.SessionContext;
import com.alexander.util.FileUtil;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Set;

@ShellComponent
public class ViewPresetsCommand extends PresetCommand {
    protected static final String COMMAND_NAME = "viewPresets";
    protected static final String COMMAND_DESC = "View your presets";

    protected ViewPresetsCommand(ComponentFlow.Builder componentFlowBuilder,
                                 SessionContext sessionContext,
                                 FileUtil fileUtil,
                                 Terminal terminal) {
        super(componentFlowBuilder, sessionContext, fileUtil, terminal);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        return getReturnString(getUserPresetsNames());
    }


    private String getReturnString(Set<String> presetNames) {
        StringBuffer sb = new StringBuffer();
        sb.append("Presets:\n\n");
        for (String presetName : presetNames) {
            sb.append(presetName).append("\n");
        }
        return sb.toString();
    }
}
