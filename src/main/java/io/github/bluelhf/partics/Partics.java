package io.github.bluelhf.partics;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.moderocky.mask.template.BukkitPlugin;
import io.github.bluelhf.partics.command.ParticsCommand;
import io.github.bluelhf.partics.util.JsonTreeWriter;
import io.github.bluelhf.partics.util.MathUtil;
import io.github.bluelhf.partics.util.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class Partics extends BukkitPlugin {


    ParticleUtil particleUtil = new ParticleUtil();

    private File defaultFile;

    ArrayList<Partic> partics = new ArrayList<>();
    BukkitTask displayTask;

    @Override
    public void startup() {
        defaultFile = new File(getDataFolder(), "partics.json");
        createDisplayTask();

        defaultFile.getParentFile().mkdirs();
        try {
            boolean isNew = defaultFile.createNewFile();
            if (!isNew) loadPartics(defaultFile);
        } catch (Exception e) {
            getLogger().warning("Failed to create or read default partics file!");
            e.printStackTrace();
        }
    }

    @Override
    public void disable() {
        cancelDisplayTask();
        particleUtil = null;
        try {
            Files.write(defaultFile.toPath(), new byte[]{}, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            savePartics(defaultFile);
        } catch (Exception e) {
            getLogger().warning("Failed to write default partics file!");
            e.printStackTrace();
        }
    }

    @Override
    protected void registerCommands() {
        register(new ParticsCommand());
    }

    /**
     * Finds a loaded {@link Partic} by its hash code
     * @param hashCode The hash code to look for
     * @return An {@link Optional} representing the found Partic.
     * */
    public Optional<Partic> findByHashCode(int hashCode) {
        return partics.stream().filter(p -> p.hashCode() == hashCode).findFirst();
    }

    /**
     * Loads {@link Partic}s from a given {@link File}
     * @param file The file to load the Partics from.
     * @throws FileNotFoundException If the file does not exist
     * @throws JsonIOException If an IO Exception occurs while parsing
     * @throws JsonSyntaxException If the JSON file is malformed
     * @throws IllegalStateException If the parsed JSON is not valid.
     * @see Partics#savePartics(File)
     * */
    public void loadPartics(File file) throws FileNotFoundException, JsonIOException, JsonSyntaxException, IllegalStateException {
        JsonElement element = new JsonParser().parse(new BufferedReader(new FileReader(file)));
        if (!element.isJsonArray()) return;
        for(JsonElement particElement : element.getAsJsonArray())
            partics.add(Partic.fromJSON(particElement.getAsJsonObject()));
        createDisplayTask();
    }

    /**
     * Saves all loaded {@link Partic}s into a given {@link File}
     * @param file The file to save the Partics into
     * @throws IOException If writing to the file or the JSON object fails.
     * @see Partics#loadPartics(File)
     * */
    public void savePartics(File file) throws IOException {
        boolean newFile = file.createNewFile();
        JsonArray jsonArray = new JsonArray();
        for (Partic p : partics)
            jsonArray.add(p.serialise());

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
            StringWriter sw = new StringWriter();
            JsonWriter writer = new JsonWriter(sw);
            writer.setIndent("    ");
            Streams.write(jsonArray, writer);
            fileWriter.write(sw.toString());
        }
    }

    /**
     * Stops displaying the loaded {@link Partic}s
     * */
    public void cancelDisplayTask() {
        if (displayTask != null && !displayTask.isCancelled()) displayTask.cancel();
    }
    /**
     * Cancels any old display tasks and re-starts displaying {@link Partic}s
     * The rate at which an attempt to display a Partic is made is equal to the GCD of all Partics' periods.
     * If the amount of loaded Partics is 0, this call is equivalent to {@link Partics#cancelDisplayTask()}
     * */
    public void createDisplayTask() {
        cancelDisplayTask();
        if (partics.size() <= 0) return;
        final int period = MathUtil.gcd(partics.stream().mapToInt(Partic::getPeriod));
        AtomicInteger ticks = new AtomicInteger();
        displayTask = new BukkitRunnable() {@Override public void run() {
            ticks.addAndGet(period);
            for (Partic p : getPartics().stream().filter(p -> ticks.get() % p.getPeriod() == 0).collect(Collectors.toList()))
                p.display();

        }}.runTaskTimer(this, 0, period);
    }

    public ParticleUtil getPUtil() {
        return particleUtil;
    }

    public void addPartic(Partic p) {
        partics.add(p);
        createDisplayTask();
    }
    public ArrayList<Partic> getPartics() {
        return new ArrayList<>(partics);
    }

    public static Partics get() {
        return (Partics) JavaPlugin.getProvidingPlugin(Partics.class);
    }
}
