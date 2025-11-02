package com.alexander.cmd.commands.preset;

import com.alexander.SessionContext;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.nio.file.Path;

@ShellComponent
public class RemovePresetCommand extends PresetCommand {
    protected static final String COMMAND_NAME = "rmPreset";
    protected static final String COMMAND_DESC = "Remove a preset";

    protected RemovePresetCommand(ComponentFlow.Builder componentFlowBuilder,
                                  SessionContext sessionContext,
                                  FileUtil fileUtil,
                                  Terminal terminal) {
        super(componentFlowBuilder, sessionContext, fileUtil, terminal);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        String presetName = getPresetName();
        boolean confirmation = confirmDelete(presetName);
        if (confirmation) {
            Path path = Path.of(fileUtil.APP_DATA_PRESETS.toString(),
                    sessionContext.getCurrentUsernameAndId(),
                    presetName + FileExtension.JSON.getValue());
            fileUtil.deleteFile(path);
            return getReturnString(presetName);
        }
        return getReturnString(presetName);
    }

    private String getReturnString(String presetName) {
        return String.format("Preset %s has been removed", presetName);
    }

    private boolean confirmDelete(String presetName) {
        ComponentFlow flow = componentFlowBuilder.reset()
                .withConfirmationInput("confirm-input")
                .name(String.format("Are you sure you want to delete %s?", presetName))
                .defaultValue(false)
                .and()
                .build();
        ComponentFlow.ComponentFlowResult result = flow.run();
        return result.getContext().get("confirm-input");
    }

}
