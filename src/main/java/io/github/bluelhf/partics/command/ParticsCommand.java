package io.github.bluelhf.partics.command;

import com.moderocky.mask.command.*;
import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.partics.Partics;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class ParticsCommand extends Commander<CommandSender> implements WrappedCommand  {
    @Override
    protected CommandImpl create() {
        return command("partics")
            .arg("create", arg((sender, input) -> {
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


                    int count     = input.length >= 2 ? (int) input[1] : 1;

                    double speed  = input.length >= 3 ? (double) input[2] : 0;
                    double dX     = input.length >= 4 ? (double) input[3] : 0;
                    double dY     = input.length >= 5 ? (double) input[4] : 0;
                    double dZ     = input.length >= 6 ? (double) input[5] : 0;

                    boolean force = input.length >= 7 && (boolean) input[6];



                },
                new ArgString().setLabel("type").setRequired(true),
                new ArgInteger().setLabel("count").setRequired(false),
                new ArgNumber().setLabel("speed").setRequired(false),
                new ArgNumber().setLabel("delta_x").setRequired(false),
                new ArgNumber().setLabel("delta_y").setRequired(false),
                new ArgNumber().setLabel("delta_z").setRequired(false),
                new ArgBoolean().setLabel("force").setRequired(false)
                )
            );
    }

    @Override
    public CommandSingleAction<CommandSender> getDefault() {
        return null;
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
        return false;
    }
}
