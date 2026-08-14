package dev.exefile7f.rheniumcore.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import static dev.exefile7f.rheniumcore.RheniumCore.LOGGER;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi{

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Title"))
                    .solidBackground();
            ConfigEntryBuilder toggle = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Text.of("catagory"));
            general.addEntry(toggle.startBooleanToggle(Text.of("switch"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer((value) ->{
                        LOGGER.info(String.valueOf(value));
                    }).build());
            return builder.build();
        };
    }
}
