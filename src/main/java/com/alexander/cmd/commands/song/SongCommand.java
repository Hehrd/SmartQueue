package com.alexander.cmd.commands.song;

import com.alexander.SessionContext;
import com.alexander.cmd.commands.Command;
import com.alexander.service.QueueService;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;

public abstract class SongCommand extends Command {

    protected final QueueService queueService;

    protected SongCommand(ComponentFlow.Builder componentFlowBuilder,
                          SessionContext sessionContext,
                          QueueService queueService,
                          Terminal terminal) {
        super(componentFlowBuilder, sessionContext, terminal);
        this.queueService = queueService;
    }

}
