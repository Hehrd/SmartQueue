package com.alexander.cmd.commands.preset;

import com.alexander.SessionContext;
import com.alexander.cmd.commands.song.EditSongCommand;
import com.alexander.model.Preset;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import com.alexander.util.JsonUtil;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class EditPresetCommand extends PresetCommand {
    protected static final String COMMAND_NAME = "editPreset";
    protected static final String COMMAND_DESC = "Edit the properties of a preset";

    private final EditSongCommand editSongCommand;

    protected EditPresetCommand(ComponentFlow.Builder componentFlowBuilder,
                                SessionContext sessionContext,
                                FileUtil fileUtil,
                                EditSongCommand editSongCommand,
                                Terminal terminal) {
        super(componentFlowBuilder, sessionContext, fileUtil, terminal);
        this.editSongCommand = editSongCommand;
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        String presetName = getPresetName();
        Preset preset = getPresetObject(presetName);
        editPreset(preset);
        return "";
    }

    private void editPreset(Preset preset) throws IOException, ParseException, SpotifyWebApiException {
        while (true) {
            String propertyToEdit = chooseProperty(preset);
            if (propertyToEdit.equalsIgnoreCase("Exit")) {
                break;
            }
            if (propertyToEdit.equalsIgnoreCase("name")) {
                renamePreset(preset);
            }
            if (propertyToEdit.equalsIgnoreCase("songs")) {
                editSongs(preset);
            }
        }

    }

    private void renamePreset(Preset preset) throws IOException, ParseException, SpotifyWebApiException {
        ComponentFlow flow = componentFlowBuilder.reset()
                .withStringInput("new-value-input")
                .defaultValue(preset.getName())
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
        ComponentFlow.ComponentFlowResult result = flow.run();
        String newName = result.getContext().get("new-value-input");
        String oldName = preset.getName();
        Path oldPath = Path.of(fileUtil.APP_DATA_PRESETS.toString(),
                sessionContext.getCurrentUsernameAndId(),
                oldName + FileExtension.JSON.getValue());
        if(!fileUtil.renameFile(oldPath, newName, FileExtension.JSON)) {
            terminal.writer().println(String.format("Failed to rename %s to %s", oldName, newName));
            return;
        }
        preset.setName(newName);
        Path newPath = Path.of(oldPath.getParent().toString(),
                newName + FileExtension.JSON.getValue());
        fileUtil.writeInFile(newPath, JsonUtil.serialize(preset), true);
    }

    private void editSongs(Preset preset) throws IOException, ParseException, SpotifyWebApiException {
        String songUri = editSongCommand.chooseSong(preset.getSongsList());
        editSongCommand.editSong(preset.getSong(songUri));
        Path path = Path.of(fileUtil.APP_DATA_PRESETS.toString(),
                sessionContext.getCurrentUsernameAndId(),
                preset.getName() + FileExtension.JSON.getValue());
        fileUtil.writeInFile(path, JsonUtil.serialize(preset), true);
    }

    private String chooseProperty(Preset preset) {
        List<SelectItem> items = new ArrayList<>();
        items.add(SelectItem.of(String.format("Name: %s", preset.getName()), "name"));
        items.add(SelectItem.of(String.format("Songs: %s", preset.getTotalSongs()), "songs"));
        items.add(SelectItem.of("Exit", "Exit"));

        ComponentFlow propertyFlow = componentFlowBuilder.reset()
                .withSingleItemSelector("property-input")
                .name("Choose a property to edit:")
                .selectItems(items)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
        ComponentFlow.ComponentFlowResult result = propertyFlow.run();
        String propertyToEdit = result.getContext().get("property-input");
        return propertyToEdit;
    }


}
