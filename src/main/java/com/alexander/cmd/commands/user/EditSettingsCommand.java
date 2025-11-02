package com.alexander.cmd.commands.user;

import com.alexander.SessionContext;
import com.alexander.cmd.commands.Command;
import com.alexander.model.Settings;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import com.alexander.util.JsonUtil;
import org.apache.hc.core5.http.ParseException;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class EditSettingsCommand extends Command {
    protected static final String COMMAND_NAME = "editSettings";
    protected static final String COMMAND_DESC = "Edit the user settings";

    private final FileUtil fileUtil;

    protected EditSettingsCommand(ComponentFlow.Builder componentFlowBuilder,
                                  SessionContext sessionContext,
                                  FileUtil fileUtil,
                                  Terminal terminal) {
        super(componentFlowBuilder, sessionContext, terminal);
        this.fileUtil = fileUtil;
    }

    @Override
    @ShellMethod(key = COMMAND_NAME, value = COMMAND_DESC)
    public String execute() throws IOException, ParseException, SpotifyWebApiException {
        Settings settings = sessionContext.getSettings();
        while (true) {
            ComponentFlow.ComponentFlowResult result = getEditSettingFlow(settings).run();
            String propertyToEdit = result.getContext().get("property-input");
            if (propertyToEdit.equalsIgnoreCase("Exit")) {
                break;
            }
            String newValue = result.getContext().get("new_value-input");
            if (!newValue.equalsIgnoreCase("old value")) {
                settings.setProperty(propertyToEdit, newValue);
            }
        }
        String usernameAndId = sessionContext.getCurrentUsernameAndId();
        Path settingsPath = Path.of(fileUtil.APP_DATA_SETTINGS.toString(),
                usernameAndId + FileExtension.JSON.getValue());
        fileUtil.writeInFile(settingsPath, JsonUtil.serialize(settings), true);
        return "";
    }

    private ComponentFlow getEditSettingFlow(Settings settings) {
        List<SelectItem> items = new ArrayList<>();
        items.add( SelectItem.of(String.format("Default Queue Name: %s", settings.getDefaultQueueName()) , "defaultQueueName" ));
        items.add( SelectItem.of(String.format("Default Queue Size: %s", settings.getDefaultQueueSize()), "defaultQueueSize"));
        items.add( SelectItem.of( String .format("Allow Repeats: %s", settings.isAllowRepeats()), "allowRepeats"));

        items.add(SelectItem.of("Exit", "Exit"));
        return componentFlowBuilder.reset()
                .withSingleItemSelector("property-input")
                .selectItems(items)
                .name("Choose a setting to edit:")
                .next(allowCancel("new_value-input"))
                .and()
                .withStringInput("new_value-input")
                .defaultValue("old value")
                .name("Enter a new value for the setting")
                .and()
                .build();
    }
}
