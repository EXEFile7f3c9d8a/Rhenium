package dev.exefile7f.rheniumcore.client;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rheniumcore")
public class ModSettings implements ConfigData{
    @ConfigEntry.Category("general_settings")
    @ConfigEntry.Gui.Tooltip
    public boolean enableCustomParticles = true;

    @ConfigEntry.Category("general_settings")
//    @ConfigEntry.Gui.(max = 100, min = 1)
    public int particleSpeed = 50;

    @ConfigEntry.Category("advanced_settings")
    public String serverIpOverride = "127.0.0.1";
}
