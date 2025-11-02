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

@ShellComponent
public class RenamePresetCommand extends PresetCommand {

    protected static final String COMMAND_NAME = "renamePreset";
    protected static final String COMMAND_DESC = "Rename a preset";

    protected RenamePresetCommand(ComponentFlow.Builder componentFlowBuilder,
                                  SessionContext sessionContext,
                                  FileUtil fileUtil,
                                  Terminal terminal) {
        super(componentFlowBuilder, sessionContext, fileUtil, terminal);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {

        return "";
    }
}
