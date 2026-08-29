package dev.exefile7f.rheniumcore.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.io.IOException;

import static dev.exefile7f.rheniumcore.client.RheniumCoreClient.config;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi{

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Title"));
            ConfigEntryBuilder toggle = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Text.of("catagory"));
            String name = "switch";
            general.addEntry(toggle.startBooleanToggle(Text.of(name), config.get(name) == null ? false : (boolean)config.get(name).getValue())
                    .setDefaultValue(false)
                    .setSaveConsumer((value) -> {
                        config.save(name, value);
                        try{
                            config.write();
                        }catch(IOException e){
                            throw new RuntimeException(e);
                        }
                    }).build());
            return builder.build();
        };
    }
}
