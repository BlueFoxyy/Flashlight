package me.foxyy.commands;

import me.foxyy.Flashlight;
import me.foxyy.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FlashlightConfigCommand implements CommandExecutor, TabCompleter {

    List<String> parameters = new ArrayList<>();

    public FlashlightConfigCommand() {
        parameters.add("depth");
        parameters.add("degree");
        parameters.add("brightness");
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command c,
                             @NonNull String label,
                             @NonNull String @NonNull [] args) {

        if (args.length == 0) {
            return false;
        }

        args[0] = args[0].toLowerCase();

        if (!parameters.contains(args[0]))
            return false;
        if (args.length == 1) {
            // query
            ChatUtils.sendPrefixMessage(sender,
                    args[0] + " is set to " + ChatColor.translateAlternateColorCodes('&', "&e")
                    + Objects.requireNonNull(Flashlight.getInstance().getMainConfig().get(args[0]))
                    + ChatColor.translateAlternateColorCodes('&', "&r")
            );
            return true;
        }
        switch (args[0]) {
            case "depth" -> {
                int depth;
                try {
                    depth = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return false;
                }
                if (depth < 1 || depth > 40) {
                    ChatUtils.error(sender, "Input value must be between 1 and 40.");
                    return false;
                }
                Flashlight.getInstance().getMainConfig().set(args[0], depth);
                ChatUtils.success(sender, args[0] + " has been set to " + depth + "!");
            }
            case "degree" -> {
                int degree;
                try {
                    Flashlight.getInstance().getLogger().info(args[1]);
                    degree = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return false;
                }
                if (degree < 10 || degree > 90) {
                    ChatUtils.error(sender, "Input value must be between 10 and 90.");
                    return false;
                }
                Flashlight.getInstance().getMainConfig().set(args[0], degree);
                ChatUtils.success(sender, args[0] + " has been set to " + degree + "!");
            }
            case "brightness" -> {
                int brightness;
                try {
                    brightness = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return false;
                }
                if (brightness < 0 || brightness > 15) {
                    ChatUtils.error(sender, "Input value must be between 0 and 15.");
                    return false;
                }
                Flashlight.getInstance().getMainConfig().set(args[0], brightness);
                ChatUtils.success(sender, args[0] + " has been set to " + brightness + "!");
            }
        }
        Flashlight.getInstance().saveConfig();
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      @NonNull String @NonNull [] args) {
        Arrays.setAll(args, i -> args[i].toLowerCase());

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list = parameters;
        } else if (args.length == 2 && parameters.contains(args[0])) {
            list.add("[value]");
        }
        return list;
    }
}
