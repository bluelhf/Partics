package io.github.bluelhf.partics;

import io.github.bluelhf.partics.util.ParticleUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class Partics extends JavaPlugin {

    ParticleUtil particleUtil = new ParticleUtil();

    @Override
    public void onEnable() { }

    @Override
    public void onDisable() {
        particleUtil = null;
    }

    public ParticleUtil getPUtil() {
        return particleUtil;
    }

    public Partics get() {
        return (Partics) JavaPlugin.getProvidingPlugin(Partics.class);
    }
}
