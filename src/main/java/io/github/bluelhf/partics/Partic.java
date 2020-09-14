package io.github.bluelhf.partics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.bluelhf.partics.util.JsonTreeWriter;
import io.github.bluelhf.partics.util.MathUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * A Partic represents a particle in Partics.
 * It is (de)serialisable using {@link Partic#serialise()} and {@link Partic#fromJSON(JsonObject)} respectively.
 * Partics store information about their location, type, count, delta, speed, force-state, and period.
 * */
public class Partic {
    private Location location;

    private Particle particle;
    private int count = 1;
    private Vector delta = new Vector(0,0,0);
    private double speed = 0;
    private boolean force = false;


    private int period;

    private JsonElement serialisationCache = null;
    private int hashCodeCache = -1;

    private Partic() {

    }

    public Partic(Location location, Particle particle, int count, Vector delta, double speed, boolean force, int period) {
        this.location = location;
        this.particle = particle;
        this.count = count;
        this.delta = delta;
        this.speed = speed;
        this.force = force;
        this.period = period;
    }
    public Partic(Location location, Particle particle, int period) {
        this.location = location;
        this.particle = particle;
        this.period = period;
    }

    /**
     * Displays this {@link Partic} once to the given receivers
     * @param receivers The collection of receivers
     * @see Partic#display(Player...)
     * @see Partic#display() 
     * */
    public void display(Collection<? extends Player> receivers) {
        display(receivers.toArray(new Player[0]));
    }

    /**
     * Displays this {@link Partic} once to the given receivers
     * @param receivers The receivers
     * @see Partic#display(Collection) 
     * @see Partic#display() 
     * */
    public void display(Player... receivers) {
        for (Player receiver : receivers)
            receiver.spawnParticle(particle, location, count, delta.getX(), delta.getY(), delta.getZ(), speed);
    }

    /**
     * Displays this {@link Partic} once in its world
     * @see Partic#display(Collection) 
     * @see Partic#display()
     * */
    public void display() {
        location.getWorld().spawnParticle(particle, location, count, delta.getX(), delta.getY(), delta.getZ(), speed, null, force);
    }

    /**
     * @return The period between {@link Partic#display()}s, in ticks (1/20ths of a second)
     * @see Partic#setPeriod(int) 
     * */
    public int getPeriod() {
        return period;
    }

    /**
     * Sets the period of this {@link Partic} and returns itself
     * @param period The new period
     * @return This {@link Partic}
     * @see Partic#getPeriod()
     * */
    public Partic setPeriod(int period) {
        this.period = period;
        return this;
    }

    /**
     * @return A clone of the location of this Partic
     * @see Partic#setLocation(Location)
     * */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Sets the location of this Partic and returns itself
     * @param location The new location
     * @return This {@link Partic}
     * @see Partic#getLocation()
     * */
    public Partic setLocation(Location location) {
        this.location = location;
        return this;
    }

    /**
     * @return The {@link org.bukkit.Particle} this particle creates.
     * @see Partic#setParticle(Particle) 
     * */
    public Particle getParticle() {
        return particle;
    }

    /**
     * Sets the {@link org.bukkit.Particle} this {@link Partic} should create and returns itself
     * @param particle The new {@link Particle}
     * @return This {@link Partic}
     * @see Partic#getParticle()
     * */
    public Partic setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    /**
     * @return The count of this {@link Partic}
     * @see Partic#setCount(int)
     * */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count of this {@link Partic} and returns itself
     * @param count The new count
     * @return This {@link Partic}
     * @see Partic#getCount()
     * */
    public Partic setCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * @return The delta of this {@link Partic}. This is equivalent to the delta in the <a href="https://minecraft.gamepedia.com/Commands/particle">/particle</a> command
     * @see Partic#setDelta(Vector)
     * */
    public Vector getDelta() {
        return delta.clone();
    }

    /**
     * Sets the delta of this {@link Partic} and returns itself
     * @param delta The new delta
     * @return This {@link Partic}
     * @see Partic#getDelta()
     * */
    public Partic setDelta(Vector delta) {
        this.delta = delta;
        return this;
    }

    /**
     * @return The speed of this {@link Partic}
     * @see Partic#setSpeed(double)
     * */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of this {@link Partic} and returns itself
     * @param speed The new speed
     * @return This {@link Partic}
     * @see Partic#getSpeed()
     * */
    public Partic setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    /**
     * @return Whether this {@link Partic} should be forced in {@link Partic#display()}
     * @see Partic#setForce(boolean)
     * */
    public boolean isForce() {
        return force;
    }

    /**
     * Sets whether this {@link Partic} should be forced in {@link Partic#display()} and returns itself
     * @param force The new force
     * @return This {@link Partic}
     * @see Partic#isForce()
     * */
    public Partic setForce(boolean force) {
        this.force = force;
        return this;
    }


    @Override
    public String toString() {
        return "Partic{" +
            "location=" + location +
            ", particle=" + particle +
            ", count=" + count +
            ", delta=" + delta +
            ", speed=" + speed +
            ", force=" + force +
            ", period=" + period +
            '}';
    }

    public BaseComponent[] toText() {
        String x = "" + MathUtil.round(location.getX(), 2);
        String y = "" + MathUtil.round(location.getY(), 2);
        String z = "" + MathUtil.round(location.getZ(), 2);

        ComponentBuilder hoverBuilder = new ComponentBuilder();
        hoverBuilder
            .append("Particle: ")
            .append(Partics.get().getPUtil().toMCN(particle).orElse("Unknown")).color(ChatColor.GOLD)
            .append("\n").reset();
        hoverBuilder
            .append("Location: ").append("\n").reset()
              .append("  x: ").append(x).color(ChatColor.GOLD).append("\n").reset()
              .append("  y: ").append(y).color(ChatColor.GOLD).append("\n").reset()
              .append("  z: ").append(z).color(ChatColor.GOLD).append("\n").reset()
              .append("  world: ").append(location.getWorld().getName()).color(ChatColor.GOLD)
            .append("\n").reset();
        hoverBuilder.append("Period: ").append("" + period).color(ChatColor.GOLD).append("\n").reset();
        hoverBuilder.append("Count: ").append("" + count).color(ChatColor.GOLD).append("\n").reset();
        hoverBuilder.append("Speed: ").append("" + speed).color(ChatColor.GOLD).append("\n").reset();
        hoverBuilder
            .append("Delta: ").append("\n").reset()
              .append("  x: ").append("" + delta.getX()).color(ChatColor.GOLD).append("\n").reset()
              .append("  y: ").append("" + delta.getY()).color(ChatColor.GOLD).append("\n").reset()
              .append("  z: ").append("" + delta.getZ()).color(ChatColor.GOLD)
            .append("\n").reset();
        hoverBuilder.append("Force: ").append(force ? "true" : "false").color(ChatColor.GOLD).append("\n").reset();
        hoverBuilder.append("\n").reset().append("Click to teleport!").color(ChatColor.GOLD);

        ComponentBuilder builder = new ComponentBuilder("" + hashCode()).color(ChatColor.GOLD);
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverBuilder.create())));
        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/partics:partics tp " + hashCode()));

        return builder.create();
    }

    /**
     * Serialises this Partic into a {@link JsonObject}
     * @return This {@link Partic} as a {@link JsonObject}
     * @see Partic#fromJSON(JsonObject)
     * @throws IOException If an IOException occurs while writing the {@link JsonObject}
     * */
    public JsonObject serialise() throws IOException {
        if (hashCode() == hashCodeCache) return serialisationCache.getAsJsonObject();
        JsonTreeWriter jsonWriter = new JsonTreeWriter();
        jsonWriter.setIndent("    ");

        jsonWriter.beginObject();
            jsonWriter.name("location").beginObject();
                jsonWriter.name("x").value(location.getX());
                jsonWriter.name("y").value(location.getY());
                jsonWriter.name("z").value(location.getZ());
                jsonWriter.name("yaw").value(location.getYaw());
                jsonWriter.name("pitch").value(location.getPitch());
                jsonWriter.name("world").value(location.getWorld().getUID().toString());
            jsonWriter.endObject();
            jsonWriter.name("period").value(period);
            jsonWriter.name("particle").beginObject();
                jsonWriter.name("type").value(particle.name());
                jsonWriter.name("count").value(count);
                jsonWriter.name("delta").beginObject();
                    jsonWriter.name("x").value(delta.getX());
                    jsonWriter.name("y").value(delta.getY());
                    jsonWriter.name("z").value(delta.getZ());
                jsonWriter.endObject();
                jsonWriter.name("speed").value(speed);
                jsonWriter.name("force").value(force);
            jsonWriter.endObject();
        jsonWriter.endObject();

        JsonElement element = jsonWriter.get();
        serialisationCache = element;
        hashCodeCache = hashCode();

        jsonWriter.close();
        return element.getAsJsonObject();
    }

    /**
     * Creates a {@link Partic} from a {@link JsonObject}
     * @param json The {@link JsonObject}
     * @return The deserialised {@link Partic}
     * @see Partic#serialise()
     * */
    public static Partic fromJSON(JsonObject json) {
        Partic partic = new Partic();

        JsonObject locObject = json.getAsJsonObject("location");
        partic.setLocation(new Location(
                Bukkit.getWorld(UUID.fromString(locObject.get("world").getAsString())),
                locObject.get("x").getAsDouble(),
                locObject.get("y").getAsDouble(),
                locObject.get("z").getAsDouble(),
                locObject.get("yaw").getAsFloat(),
                locObject.get("pitch").getAsFloat()
        ));

        partic.setPeriod(json.get("period").getAsInt());

        JsonObject particleObject = json.getAsJsonObject("particle");
        partic.setParticle(Particle.valueOf(particleObject.get("type").getAsString()));
        partic.setCount(particleObject.get("count").getAsInt());

        JsonObject deltaObject = particleObject.getAsJsonObject("delta");
        partic.setDelta(new Vector(
                deltaObject.get("x").getAsDouble(),
                deltaObject.get("y").getAsDouble(),
                deltaObject.get("z").getAsDouble()
        ));

        partic.setSpeed(particleObject.get("speed").getAsDouble());
        partic.setForce(particleObject.get("force").getAsBoolean());

        return partic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Partic partic = (Partic) o;

        if (count != partic.count) return false;
        if (Double.compare(partic.speed, speed) != 0) return false;
        if (force != partic.force) return false;
        if (period != partic.period) return false;
        if (!location.equals(partic.location)) return false;
        if (particle != partic.particle) return false;
        return delta.equals(partic.delta);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = location.hashCode();
        result = 31 * result + particle.hashCode();
        result = 31 * result + count;
        result = 31 * result + delta.hashCode();
        temp = Double.doubleToLongBits(speed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (force ? 1 : 0);
        result = 31 * result + period;
        return result;
    }
}