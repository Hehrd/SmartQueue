package com.alexander.cmd.commands.song;

import com.alexander.SessionContext;
import com.alexander.model.SongWithProperties;
import com.alexander.service.QueueService;
import com.alexander.model.Song;
import com.alexander.util.WeightedRandomPicker;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.*;

@ShellComponent
public class GenerateQueueCommand extends SongCommand {
    protected static final String COMMAND_NAME = "genQueue";
    protected static final String COMMAND_DESC = "Generate a queue";
    private final WeightedRandomPicker<SongWithProperties> randomPicker;

    @Autowired
    protected GenerateQueueCommand(ComponentFlow.Builder componentFlowBuilder,
                                   SessionContext sessionContext,
                                   QueueService queueService,
                                   Terminal terminal, WeightedRandomPicker<SongWithProperties> randomPicker) {
        super(componentFlowBuilder, sessionContext, queueService, terminal);
        this.randomPicker = randomPicker;
    }


    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, SpotifyWebApiException, ParseException {
        List<SongWithProperties> addedSongs = sessionContext.getAddedSongsList();
        ComponentFlow flow = getQueueInformationFlow();
        ComponentFlow.ComponentFlowResult result = flow.run();
        String name = result.getContext().get("name-input");
        int size = parseQueueSize(result.getContext().get("size-input"));
        List<SongWithProperties> queue = generateQueue(addedSongs, size, name);
        return getReturnString(queue);
    }

    protected String getReturnString(List<? extends Song> queue) {
        StringBuffer sb = new StringBuffer();
        sb.append("Queue: ").append("\n\n");
        for (Song song : queue) {
            String songString = song.toString();
            sb.append(songString).append("\n");
        }
        return sb.toString();
    }

    private ComponentFlow getQueueInformationFlow() {
        ComponentFlow flow = componentFlowBuilder.reset()
                .withStringInput("name-input")
                .name("Name the queue playlist: \n")
                .defaultValue(sessionContext.getSettings().getDefaultQueueName())
                .and()
                .withStringInput("size-input")
                .name("Size of the queue playlist: \n")
                .defaultValue(String.valueOf(sessionContext.getSettings().getDefaultQueueSize()))
                .and()
                .build();
        return flow;
    }

    private List<SongWithProperties> generateQueue(List<SongWithProperties> songs, int size, String name) throws IOException, ParseException, SpotifyWebApiException {
        List<SongWithProperties> songPool = new ArrayList<>(songs);
        List<SongWithProperties> queue = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        boolean allowRepeats = sessionContext.getSettings().isAllowRepeats();
        List<Integer> weights = getSongsWeights(songs);
        int i = 0;
        while (i < size && !songPool.isEmpty()) {
            SongWithProperties randomSong = randomPicker.pick(songPool, weights, new Random());
            queue.add(randomSong);
            uris.add(randomSong.getUri());
            if (!allowRepeats ||
                    randomSong.getMaxRepeats() == Collections.frequency(uris, randomSong.getUri())) {
                int randomSongIndex = songPool.indexOf(randomSong);
                songPool.remove(randomSong);
                weights.remove(randomSongIndex);
            }
            i++;
        }
        String playlistId = queueService.createPlaylist(name);
        queueService.addSongsToPlaylist(playlistId, uris);
        return queue;
    }



    private List<Integer> getSongsWeights(List<SongWithProperties> songs) {
        List<Integer> weights = new ArrayList<>();
        for (SongWithProperties song : songs) {
            weights.add(song.getWeight());
        }
        return weights;
    }

    private int parseQueueSize(String size) {
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            String errorString = String.format("Invalid queue size %s, using default", size);
            terminal.writer().println(errorString);
            return sessionContext.getSettings().getDefaultQueueSize();
        }
    }
}
