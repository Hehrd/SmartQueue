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
public class RemoveSongCommand extends SongCommand {
    protected static final String COMMAND_NAME = "rmSong";
    protected static final String COMMAND_DESC = "Remove a song";

    protected RemoveSongCommand(ComponentFlow.Builder componentFlowBuilder,
                                SessionContext sessionContext,
                                QueueService queueService,
                                Terminal terminal) {
        super(componentFlowBuilder, sessionContext, queueService, terminal);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        return getReturnString(removeSongs());
    }


    protected String getReturnString(List<? extends Song> removedSongs) {
        StringBuffer sb = new StringBuffer();
        sb.append("Removed Songs: ").append("\n\n");
        for (Song removedSong : removedSongs) {
            String songString = removedSong.toString();
            sb.append(songString).append("\n");
        }
        return sb.toString();
    }

    private List<SongWithProperties> removeSongs() {
        List<SongWithProperties> addedSongs = sessionContext.getAddedSongsList();
        List<SelectItem> selectItems = new ArrayList<>();
        for (Song song : addedSongs) {
            String songString = song.toString();
            String songUri = song.getUri();
            selectItems.add(SelectItem.of(songString, songUri));
        }
        ComponentFlow flow = componentFlowBuilder.reset()
                .withMultiItemSelector("songs-input")
                .name("Choose songs to remove: ")
                .selectItems(selectItems)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
        ComponentFlow.ComponentFlowResult result = flow.run();
        List<String> songsUris = result.getContext().get("songs-input");
        List<SongWithProperties> removedSongs = sessionContext.removeSongs(songsUris);
        return removedSongs;
    }



}
