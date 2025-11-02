package com.alexander.cmd.commands.song;

import com.alexander.SessionContext;
import com.alexander.service.QueueService;
import com.alexander.model.Song;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@ShellComponent
public class AddSongCommand extends SongCommand {
    protected static final String COMMAND_NAME = "addSongs";
    protected static final String COMMAND_DESC = "Add songs to the context";
    private final String DEFAULT_SONG_URI = "https://open.spotify.com/track/4PTG3Z6ehGkBFwjybzWkR8?si=e6773c64e00c4328";

    @Autowired
    protected AddSongCommand(ComponentFlow.Builder componentFlowBuilder,
                             SessionContext sessionContext,
                             QueueService queueService,
                             Terminal terminal) {
        super(componentFlowBuilder, sessionContext, queueService, terminal);
    }


    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        terminal.writer().println(String.format("Add songs to the context"));
        ComponentFlow songsFlow = getAddSongsFlow();
        ComponentFlow.ComponentFlowResult songsFlowResult = songsFlow.run();
        List<Song> songsToAdd = queueService.getSongs(songsFlowResult.getContext().get("songs-input"));
        sessionContext.addSongs(songsToAdd);
        return getReturnString(songsToAdd);
    }

    protected String getReturnString(List<Song> songsToAdd) {
        StringBuffer sb = new StringBuffer();
        if (!songsToAdd.isEmpty()) {
            sb.append("Newly Added Songs:").append("\n\n");
            for (Song song : songsToAdd) {
                sb.append(song.toString()).append("\n");
            }
            return sb.toString();
        }
        return "No songs found";
    }


    private ComponentFlow getAddSongsFlow() {
        ComponentFlow flow = componentFlowBuilder.reset()
                .withStringInput("songs-input")
                .name("Enter a link for a song, playlist or album:")
                .defaultValue(DEFAULT_SONG_URI)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
        return flow;
    }








}
