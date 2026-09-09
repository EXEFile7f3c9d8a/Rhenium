package dev.exefile7f.rheniumcore.client;

import dev.exefile7f.rheniumcore.config.Config;
import net.fabricmc.api.ClientModInitializer;

import static dev.exefile7f.rheniumcore.RheniumCore.LOGGER;
import static dev.exefile7f.rheniumcore.RheniumCore.MOD_ID;
import static dev.exefile7f.rheniumcore.RheniumCore.getConfigPath;

public class RheniumCoreClient implements ClientModInitializer{
    public static final Config config = new Config(getConfigPath());
	@Override
	public void onInitializeClient(){
        LOGGER.debug("Config Path of {}: {}", MOD_ID, getConfigPath());
    }
}