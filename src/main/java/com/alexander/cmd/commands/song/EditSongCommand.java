package com.alexander.cmd.commands.song;

import com.alexander.SessionContext;
import com.alexander.model.SongWithProperties;
import com.alexander.service.QueueService;
import com.alexander.model.Song;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class EditSongCommand extends SongCommand {
    protected static final String COMMAND_NAME = "editSong";
    protected static final String COMMAND_DESC = "Edit the properties of a song";

    protected EditSongCommand(ComponentFlow.Builder componentFlowBuilder,
                              SessionContext sessionContext,
                              QueueService queueService,
                              Terminal terminal) {
        super(componentFlowBuilder, sessionContext, queueService, terminal);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        if (sessionContext.getTotalSongs() == 0) {
            return "No added songs";
        }
        List<String> songStrings = new ArrayList<>();
        for (Song song : sessionContext.getAddedSongsList()) {
            songStrings.add(song.toString());
        }
        String songUri = chooseSong(sessionContext.getAddedSongsList());
        if (songUri.equalsIgnoreCase("Exit")) {
            return "";
        }
        SongWithProperties songToEdit = sessionContext.getAddedSong(songUri);
        editSong(songToEdit);
        return "";
    }

    public String chooseSong(List<SongWithProperties> songs) {
        if (songs.isEmpty()) {
            return null;
        }
        List<SelectItem> selectItems = new ArrayList<>();
        for (Song song : songs) {
            String songString = song.toString();
            String songUri = song.getUri();
            selectItems.add(SelectItem.of(songString, songUri));
        }
        selectItems.add(SelectItem.of("Exit", "Exit"));
        ComponentFlow flow = componentFlowBuilder.reset()
                .withSingleItemSelector("song-input")
                .selectItems(selectItems)
                .name("Choose a song to edit:")
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
        ComponentFlow.ComponentFlowResult result = flow.run();
        String uri = result.getContext().get("song-input");
        return uri;
    }

    public void editSong(SongWithProperties songToEdit) {
        while (true) {
            ComponentFlow flow = buildEditSongFlow(songToEdit);
            ComponentFlow.ComponentFlowResult result = flow.run();
            String propertyToEdit = result.getContext().get("property-input");
            if (propertyToEdit.equalsIgnoreCase("Exit")) {
                break;
            }
            String newValue = result.getContext().get("new-value-input");
            if (!newValue.equalsIgnoreCase("old value")) {
                songToEdit.setProperty(propertyToEdit, newValue);
            }
        }
    }

    private ComponentFlow buildEditSongFlow(SongWithProperties songToEdit) {
        List<SelectItem> items = new ArrayList<>();
        items.add(SelectItem.of(String.format("Weight: %s", songToEdit.getWeight()), "weight"));
        items.add(SelectItem.of(String.format("Max Repeats: %s", songToEdit.getMaxRepeats()), "maxRepeats"));
        items.add(SelectItem.of("Exit", "Exit"));
        ComponentFlow flow = componentFlowBuilder.reset()
                .withSingleItemSelector("property-input")
                .name("Choose a property to edit:")
                .selectItems(items)
                .resultMode(ResultMode.ACCEPT)
                .next(allowCancel("new-value-input"))
                .and()
                .withStringInput("new-value-input")
                .defaultValue("old value")
                .name("Set the new property value")
                .and()
                .build();
        return flow;
    }

//    private ComponentFlow getEditPropertyFlow(String oldValue) {
//        ComponentFlow flow = componentFlowBuilder.reset()
//                .withStringInput("new_value-input")
//                .defaultValue(oldValue)
//                .name("Give the new property value")
//                .and()
//                .build();
//        return flow;
//    }
}
