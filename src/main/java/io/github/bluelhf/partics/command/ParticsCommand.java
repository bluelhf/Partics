package io.github.bluelhf.partics.command;

import com.moderocky.mask.command.*;
import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.partics.Partic;
import io.github.bluelhf.partics.Partics;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


public class ParticsCommand extends Commander<CommandSender> implements WrappedCommand {
    @Override
    protected CommandImpl create() {
        return command("partics")
            .arg("create",
                arg((sender, input) -> {
                        Method getLocationMethod;
                        Location location;
                        try {
                            getLocationMethod = sender.getClass().getMethod("getLocation");
                            getLocationMethod.setAccessible(true);
                            location = (Location) getLocationMethod.invoke(sender);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            sender.spigot().sendMessage(
                                new ComponentBuilder()
                                    .append("Cannot get location of an instance of ").color(ChatColor.RED)
                                    .append(sender.getClass().getSimpleName())
                                    .append("!")
                                    .create()
                            );
                            return;
                        }

                        String typeStr = (String) input[0];
                        Optional<Particle> maybeParticle = Partics.get().getPUtil().fromMCN(typeStr);

                        if (maybeParticle.isEmpty()) {
                            sender.spigot().sendMessage(
                                new ComponentBuilder()
                                    .append("'").append(typeStr).append("'")
                                    .append(" is not a valid particle!").color(ChatColor.RED)
                                    .create()
                            );
                            return;
                        }


                        int period = (int) input[1];

                        int count = input.length >= 3 ? (int) input[2] : 1;

                        double speed = input.length >= 4 ? (double) input[3] : 0;
                        double dX = input.length >= 5 ? (double) input[4] : 0;
                        double dY = input.length >= 6 ? (double) input[5] : 0;
                        double dZ = input.length >= 7 ? (double) input[6] : 0;

                        boolean force = input.length >= 8 && (boolean) input[7];

                        Partic partic = new Partic(location, maybeParticle.get(), count, new Vector(dX, dY, dZ), speed, force, period);
                        Partics.get().addPartic(partic);
                        try {
                            Partics.get().getLogger().info("Serialised Partic is " + partic.serialise().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    new ArgString() {@Override public @Nullable List<String> getCompletions() {
                        return new ArrayList<>(Partics.get().getPUtil().getMCNs());
                    }}.setLabel("type").setRequired(true),
                    new ArgInteger() {@Override public @Nullable List<String> getCompletions() {
                        return Arrays.asList("1", "2", "5", "20", "40", "100");
                    }}.setLabel("period").setRequired(true),
                    new ArgInteger() {@Override public @Nullable List<String> getCompletions() {
                        return Arrays.asList("1", "2", "5", "10", "100", "1000");
                    }}.setLabel("count").setRequired(false),
                    new ArgNumber() {@Override public @Nullable List<String> getCompletions() {
                        return Arrays.asList("0", "0.05", "0.1", "1", "0.2");
                    }}.setLabel("speed").setRequired(false),
                    new ArgNumber() {@Override public @Nullable List<String> getCompletions() {
                        return Arrays.asList("0", "0.02", "0.1", "1");
                    }}.setLabel("delta_x").setRequired(false),
                    new ArgNumber() {@Override public @Nullable List<String> getCompletions() {
                        return Arrays.asList("0", "0.02", "0.1", "1");
                    }}.setLabel("delta_y").setRequired(false),
                    new ArgNumber() {@Override public @Nullable List<String> getCompletions() {
                        return Arrays.asList("0", "0.02", "0.1", "1");
                    }}.setLabel("delta_z").setRequired(false),
                    new ArgBoolean() {@Override public @Nullable List<String> getCompletions() {
                        return Arrays.asList("true", "false");
                    }}.setLabel("force").setRequired(false)
                )
            ).arg("list", (sender) -> {
                ArrayList<Partic> partics = Partics.get().getPartics();
                if (partics.isEmpty()) {
                    sender.spigot().sendMessage(new ComponentBuilder().append("There are no loaded partics.").color(ChatColor.GRAY).create());
                    return;
                }
                sender.spigot().sendMessage(new ComponentBuilder()
                    .append("―――――").color(ChatColor.DARK_GRAY).strikethrough(true)
                    .append(" List of Partics ").strikethrough(false).color(ChatColor.GOLD)
                    .append("―――――").color(ChatColor.DARK_GRAY).strikethrough(true)
                    .create()
                );
                int i = 0;
                for (Partic p : partics) {
                    i++;
                    ComponentBuilder messageBuilder = new ComponentBuilder(i + ". ").color(ChatColor.GRAY);
                    for (BaseComponent bc : p.toText()) messageBuilder.append(bc);
                    sender.spigot().sendMessage(messageBuilder.create());
                }
                sender.spigot().sendMessage(new ComponentBuilder(System.lineSeparator()).append("Hover over a Partic ID for more information.").color(ChatColor.GRAY).create());

            }).arg("tp", arg((sender, input) -> {
                    Optional<Partic> maybePartic = Partics.get().findByHashCode((int) input[0]);
                    if (maybePartic.isEmpty()) {
                        sender.spigot().sendMessage(new ComponentBuilder()
                            .append("No Partic with ID " + input[0] + " exists!").color(ChatColor.RED).append(System.lineSeparator()).reset()
                            .append("Use ").color(ChatColor.GRAY).append("/partics list ").color(ChatColor.GOLD).append(" to list all partics!").color(ChatColor.GRAY)
                            .create()
                        );
                        return;
                    }

                Method teleportMethod;
                try {
                    teleportMethod = sender.getClass().getMethod("teleport", Location.class);
                    teleportMethod.setAccessible(true);

                    teleportMethod.invoke(sender, maybePartic.get().getLocation());
                    sender.spigot().sendMessage(new ComponentBuilder()
                        .append("Teleported you to ").color(ChatColor.GRAY)
                        .append("Partic " + maybePartic.get().hashCode()).color(ChatColor.GOLD)
                        .create()
                    );
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    sender.spigot().sendMessage(
                        new ComponentBuilder()
                            .append("Cannot teleport an instance of ").color(ChatColor.RED)
                            .append(sender.getClass().getSimpleName())
                            .append("!")
                            .create()
                    );
                }

                }, new ArgInteger() {@Override public @Nullable List<String> getCompletions() {
                    return Partics.get().getPartics().stream().map(p -> "" + p.hashCode()).collect(Collectors.toList());
                }}.setLabel("id"))
            ).onException((sender, throwable) -> {
                throwable.printStackTrace();
                return true;
            });
    }

    @Override
    public CommandSingleAction<CommandSender> getDefault() {
        return sender -> {
            sender.spigot().sendMessage(new ComponentBuilder().append("That's not a valid command - here's some help!").color(ChatColor.GRAY).create());
            ComponentBuilder helpBuilder = new ComponentBuilder();
            helpBuilder
                .append("―――――").color(ChatColor.DARK_GRAY).strikethrough(true)
                .append(" Help for /partics ").strikethrough(false).color(ChatColor.GOLD)
                .append("―――――").color(ChatColor.DARK_GRAY).strikethrough(true)
                .append(System.lineSeparator()).reset();

            for (String pattern : getPatterns()) {
                helpBuilder
                    .append("/" + getCommand()).color(ChatColor.GRAY)
                    .append(" " + pattern).color(ChatColor.GOLD)
                    .event(getClickEvent(getCommand(), pattern))
                    .event(getHoverEvent(pattern, ChatColor.GOLD))
                    .append(System.lineSeparator()).reset();
            }
            sender.spigot().sendMessage(helpBuilder.create());
        };
    }

    private HoverEvent getHoverEvent(String pattern, ChatColor color) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(
            (pattern.contains("[") || pattern.contains("<")) ?
                "Click to suggest."
                : "Click to run.",
            color)
        ));
    }
    private ClickEvent getClickEvent(String command, String pattern) {
        return new ClickEvent(
            (pattern.contains("[") || pattern.contains("<"))
                ? ClickEvent.Action.SUGGEST_COMMAND
                : ClickEvent.Action.RUN_COMMAND,
            "/" + command + " " + pattern.replaceFirst("(<.*|\\[.*)", ""));
    }

    @Override
    public @NotNull String getCommand() {
        return "partics";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Arrays.asList("pcs", "partic");
    }

    @Override
    public @NotNull String getUsage() {
        return "No usage defined.";
    }

    @Override
    public @NotNull String getDescription() {
        return "Main command for Partics.";
    }

    @Override
    public @Nullable String getPermission() {
        return "partics.use";
    }

    @Override
    public @Nullable String getPermissionMessage() {
        return "§cNo permission!";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> strings = getTabCompletions(args);
        if (strings == null || strings.isEmpty()) return null;
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], strings, completions);
        Collections.sort(completions);
        return completions;
    }


/*    @Override
    public @Nullable List<String> getCompletions(int i, @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length <= 1) return Arrays.asList("create", "list");
        if (args[0].equalsIgnoreCase("create")) {
            switch (args.length) {
                case 2:
                    return new ArrayList<>(Partics.get().getPUtil().getMCNs());
                case 3:
                    return Arrays.asList("1", "2", "5", "10", "20", "100");
                case 4:
                    return Arrays.asList("0", "1", "10", "100");
                case 5:
                case 6:
                case 7:
                case 8:
                    return Arrays.asList("0", "0.1", "1");
                case 9:
                    return Arrays.asList("true", "false");
            }
        }
        return Collections.emptyList();
    }*/
}
