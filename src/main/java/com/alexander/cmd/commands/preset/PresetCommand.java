package com.alexander.cmd.commands.preset;

import com.alexander.SessionContext;
import com.alexander.cmd.commands.Command;
import com.alexander.model.Preset;
import com.alexander.util.FileExtension;
import com.alexander.util.FileUtil;
import com.alexander.util.JsonUtil;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PresetCommand extends Command {
    protected final FileUtil fileUtil;

    protected PresetCommand(ComponentFlow.Builder componentFlowBuilder,
                            SessionContext sessionContext,
                            FileUtil fileUtil,
                            Terminal terminal) {
        super(componentFlowBuilder, sessionContext, terminal);
        this.fileUtil = fileUtil;
    }

    protected Set<String> getUserPresetsNames() {
        Path path = Path.of(fileUtil.APP_DATA_PRESETS.toString(),
                sessionContext.getCurrentUsernameAndId());
        return fileUtil.getFileNames(path, FileExtension.JSON);
    }

    protected String getPresetName() {
        Set<String> presetNames = getUserPresetsNames();
        List<SelectItem> items = new ArrayList<>();
        for (String presetName : presetNames) {
            items.add(SelectItem.of(presetName, presetName));
        }
        ComponentFlow flow = componentFlowBuilder.reset()
                .withSingleItemSelector("name-input")
                .name("Choose preset:")
                .selectItems(items)
                .resultMode(ResultMode.ACCEPT)
                .and()
                .build();
        ComponentFlow.ComponentFlowResult result = flow.run();
        String presetName = result.getContext().get("name-input");
        return presetName;
    }

    protected Preset getPresetObject(String presetName) {
        String presetJson = fileUtil.readFromFile(Path.of(fileUtil.APP_DATA_PRESETS.toString(),
                sessionContext.getCurrentUsernameAndId(),
                presetName + FileExtension.JSON.getValue()));
        Preset preset = JsonUtil.deserialize(presetJson, Preset.class);
        return preset;
    }


}
