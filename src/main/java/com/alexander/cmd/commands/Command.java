package com.alexander.cmd.commands;

import com.alexander.SessionContext;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.component.support.SelectorItem;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public abstract class Command {
    protected static final String COMMAND_NAME = "command";
    protected static final String COMMAND_DESC = "command description";

    protected final String SINGLE_ITEM_INPUT = "single-item-input";
    protected final String MULTI_ITEM_INPUT = "multi-item-input";

    protected final ComponentFlow.Builder componentFlowBuilder;
    protected final SessionContext sessionContext;
    protected final Terminal terminal;
    private final Set<String> cancelWords;

    protected Command(ComponentFlow.Builder componentFlowBuilder,
                      SessionContext sessionContext,
                      Terminal terminal) {
        this.componentFlowBuilder = componentFlowBuilder;
        this.sessionContext = sessionContext;
        this.terminal = terminal;
        this.cancelWords = getCancelWords();
    }

    private HashSet<String> getCancelWords() {
        HashSet<String> cancelWords = new HashSet<>();
        cancelWords.add("exit");
        cancelWords.add("cancel");
        cancelWords.add("q!");
        return cancelWords;
    }

    protected Function<SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>>, String> allowCancel(String nextInput) {
        return ctxt -> {
            if (cancelWords.contains(ctxt.getValue().get().toLowerCase())) {
                return null;
            }
            return nextInput;
        };
    }


    public abstract String execute() throws IOException, ParseException, SpotifyWebApiException;

    protected String chooseItemFromCollection(String msg, Collection<String> strings) {
        List<SelectItem> items = new ArrayList<>();
        for (String string : strings) {
            items.add(SelectItem.of(string, string));
        }
        items.add(SelectItem.of( "Exit", "Exit"));
        ComponentFlow componentFlow = componentFlowBuilder.reset()
                .withSingleItemSelector(SINGLE_ITEM_INPUT)
                .name(msg)
                .selectItems(items)
                .defaultSelect("Exit")
                .and()
                .build();
        return componentFlow.run().getContext().get(SINGLE_ITEM_INPUT);

    }

    protected List<String> chooseItemsFromCollection(String msg, Collection<String> strings) {
        List<SelectItem> items = new ArrayList<>();
        for (String string : strings) {
            items.add(SelectItem.of(string, string));
        }
        ComponentFlow componentFlow = componentFlowBuilder.reset()
                .withMultiItemSelector(MULTI_ITEM_INPUT)
                .name(msg)
                .selectItems(items)
                .and()
                .build();

        return componentFlow.run().getContext().get(MULTI_ITEM_INPUT);
    }

}
