package io.github.bluelhf.partics;

import io.github.bluelhf.partics.util.ParticleUtil;
import org.bukkit.Location;
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

    public static Partics get() {
        return (Partics) JavaPlugin.getProvidingPlugin(Partics.class);
    }

    static class Particle {
        private Location location;
        private Particle particle;
        private int period;

        public Particle(Location location, Particle particle, int period) {
            this.location = location;
            this.particle = particle;
            this.period = period;
        }

        /**
         * @return The period between particle spawns, in ticks (1/20ths of a second).
         * */
        public int getPeriod() {
            return period;
        }

        /**
         * @return The location of this particle.
         * */
        public Location getLocation() {
            return location;
        }

        /**
         * @return The {@link org.bukkit.Particle} this particle creates.
         * */
        public Particle getParticle() {
            return particle;
        }
    }
}
