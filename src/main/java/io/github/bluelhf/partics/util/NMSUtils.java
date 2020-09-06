package io.github.bluelhf.partics.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class NMSUtils {
    public static String nmsVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    @Nullable
    public static Class<?> cbClass(String subPath) {
        try { return Class.forName("org.bukkit.craftbukkit." + nmsVersion() + "." + subPath);
        } catch (Exception e) { return null; }
    }

    @Nullable
    public static Class<?> nmsClass(String subPath) {
        try { return Class.forName("net.minecraft.server." + nmsVersion() + "." + subPath);
        } catch (Exception e) { return null; }
    }
}
