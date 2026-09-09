package dev.exefile7f.rheniumcore;

import dev.exefile7f.rheniumcore.api.util.Systems;
import dev.exefile7f.rheniumcore.api.util.threadpool.ThreadPool;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class RheniumCore implements ModInitializer{
	public static final String MOD_ID = "rheniumcore";
    public static final String VERSION = "0.0.0-alpha";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MixinComputesReg MIXIN_REG = new MixinComputesReg();
    public static ThreadPool THREAD_POOL = new ThreadPool(MIXIN_REG.getInputMethods());

	@Override
	public void onInitialize(){
        THREAD_POOL.launchThreads();
        LOGGER.info("RheniumCore running! Version {}. Available cores:{}", VERSION, Systems.getCores());
	}
    public static Path getConfigPath(){
        return Path.of(FabricLoader.getInstance().getConfigDir().toString(), "\\" + RheniumCore.MOD_ID + "\\" + RheniumCore.MOD_ID + ".json") ;
    }
}
