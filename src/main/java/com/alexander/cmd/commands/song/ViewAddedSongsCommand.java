package com.alexander.cmd.commands.song;

import com.alexander.SessionContext;
import com.alexander.service.QueueService;
import com.alexander.model.Song;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@ShellComponent
public class ViewAddedSongsCommand extends SongCommand {
    protected static final String COMMAND_NAME = "viewSongs";
    protected static final String COMMAND_DESC = "View added songs";

    @Autowired
    protected ViewAddedSongsCommand(ComponentFlow.Builder componentFlowBuilder,
                                    SessionContext sessionContext,
                                    QueueService queueService,
                                    Terminal terminal) {
        super(componentFlowBuilder, sessionContext, queueService, terminal);
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        return getReturnString(sessionContext.getAddedSongsList());
    }

    protected String getReturnString(List<? extends Song> addedSongs) {
        StringBuffer sb = new StringBuffer();
        sb.append("Added Songs:").append("\n\n");
        for (Song song : addedSongs) {
            String songString = song.toString();
            sb.append(songString).append("\n");
        }
        return sb.toString();
    }
}
