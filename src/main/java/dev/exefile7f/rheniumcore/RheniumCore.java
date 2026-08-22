package dev.exefile7f.rheniumcore;

import dev.exefile7f.rheniumcore.util.threadpool.ThreadPool;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.exefile7f.rheniumcore.statics.StaticResource.*;

public class RheniumCore implements ModInitializer {
	public static final String MOD_ID = "rheniumcore";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ThreadPool THREAD_POOL = new ThreadPool();

	@Override
	public void onInitialize() {
        LOGGER.info("RheniumCore running! Version 1.0.0.000-alpha. Available cores:{}", CPU_CORES);
        THREAD_POOL.launchThreads();
	}
}
