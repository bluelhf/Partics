package io.github.bluelhf.partics.util;

import org.bukkit.Particle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This utility class is non-static because we wish to precompute the MCN-Particle mappings. It can be acquired via Partics#getPUtil
 * */
public class ParticleUtil {
    private HashMap<String, Particle> mcnParticleMap = new HashMap<>();
    public ParticleUtil() {
        for (Particle particle : Particle.values()) {
            try {
                // Get CraftParticle class and instance
                Class<?> craftParticleClass = NMSUtils.cbClass("CraftParticle");
                Object craftParticle = craftParticleClass.cast(particle);

                // Get CraftParticle#minecraftKey
                Field minecraftKeyField = craftParticleClass.getDeclaredField("minecraftKey");
                minecraftKeyField.setAccessible(true);

                // Get minecraftKey
                Object minecraftKey = minecraftKeyField.get(craftParticle);

                // Get MCN
                Method getKeyMethod = minecraftKey.getClass().getDeclaredMethod("getKey");
                getKeyMethod.setAccessible(true);
                String mcn = (String) getKeyMethod.invoke(minecraftKey);

                mcnParticleMap.put(mcn, particle);

            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NullPointerException e) {
                System.out.println("[Partics] An error occurred while trying to register MCN<->Bukkit map!");
                e.printStackTrace();
            }
        }
    }

    public Optional<Particle> fromMCN(String mcn) {
        return Optional.of(mcnParticleMap.get(mcn));
    }

    public Optional<String> toMCN(Particle particle) {
        return mcnParticleMap.entrySet().stream().filter(e -> e.getValue() == particle).findFirst().map(Map.Entry::getKey);
    }
}
