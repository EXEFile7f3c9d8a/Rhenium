package dev.exefile7f.rheniumcore.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class RheniumCoreClient implements ClientModInitializer{
	@Override
	public void onInitializeClient(){
        AutoConfig.register(ModSettings.class, GsonConfigSerializer::new);
    }
}