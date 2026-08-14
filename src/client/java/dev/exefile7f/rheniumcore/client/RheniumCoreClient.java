package dev.exefile7f.rheniumcore.client;

import dev.exefile7f.rheniumcore.statics.StaticResource;
import net.fabricmc.api.ClientModInitializer;

import static dev.exefile7f.rheniumcore.RheniumCore.LOGGER;
import static dev.exefile7f.rheniumcore.RheniumCore.MOD_ID;

public class RheniumCoreClient implements ClientModInitializer{
	@Override
	public void onInitializeClient(){
        LOGGER.debug("Config Path of {}: {}", MOD_ID, StaticResource.CONFIG_PATH);
    }
}