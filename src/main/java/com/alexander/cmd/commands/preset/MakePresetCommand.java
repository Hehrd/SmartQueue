package com.alexander.cmd.commands.preset;

import com.alexander.SessionContext;
import com.alexander.model.Preset;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import com.alexander.util.JsonUtil;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ComponentFlow.ComponentFlowResult;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

@ShellComponent
public class MakePresetCommand extends PresetCommand {
    protected static final String COMMAND_NAME = "mkPreset";
    protected static final String COMMAND_DESC = "Make a preset of songs";

    protected MakePresetCommand(ComponentFlow.Builder componentFlowBuilder,
                                SessionContext sessionContext,
                                Terminal terminal,
                                FileUtil fileUtil) {
        super(componentFlowBuilder, sessionContext, fileUtil, terminal);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        Set<String> presets = getUserPresetsNames();
        ComponentFlow componentFlow = getNamePresetFlow(presets.size() + 1);
        ComponentFlowResult result = componentFlow.run();
        String name = result.getContext().get("name-input");
        if (presets.contains(name)) {
            return String.format("Preset %s already exists", name);
        }
        Preset preset = new Preset(name,sessionContext.getTotalSongs(), sessionContext.getAddedSongsMap());
        String presetJson = JsonUtil.serialize(preset);
        Path path = Path.of(fileUtil.APP_DATA_PRESETS.toString(),
                sessionContext.getCurrentUsernameAndId(),
                name + FileExtension.JSON.getValue());
        fileUtil.writeInFile(path, presetJson, true);
        return getReturnString(name);
    }

    protected String getReturnString(String name) {
        return String.format("Preset %s created",  name) ;
    }

    private ComponentFlow getNamePresetFlow(int presetNumber) {
        return componentFlowBuilder.reset()
                .withStringInput("name-input")
                .name("Enter a name for the preset:")
                .defaultValue(String.format("preset-%s-%s", presetNumber, LocalDateTime.now()))
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
    }


}
