package dev.exefile7f.rheniumcore;

import dev.exefile7f.rheniumcore.util.threadpool.ThreadPool;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.exefile7f.rheniumcore.statics.StaticResource.*;

public class RheniumCore implements ModInitializer{
	public static final String MOD_ID = "rheniumcore";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MixinComputesReg MIXIN_REG = new MixinComputesReg();
    public static ThreadPool THREAD_POOL = new ThreadPool(MIXIN_REG.getInputMethods());
    public static String VERSION;
	@Override
	public void onInitialize(){
        THREAD_POOL.launchThreads();
        VERSION = "0.0.0-alpha";
        LOGGER.info("RheniumCore running! Version {}. Available cores:{}", VERSION, CPU_CORES);
	}
}
