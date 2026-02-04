package me.foxyy.commands;

import me.foxyy.Flashlight;
import me.foxyy.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

        if (args.length == 0 || args.length > 3) {
            return false;
        }

        String target = args[0];
        String configPath = args[1].toLowerCase();

        if (!parameters.contains(configPath)) {
            return false;
        }

        String configValue;
        if (args.length == 3) {
            configValue = args[2];
        } else {
            // query
            ChatUtils.sendPrefixMessage(sender,
                    configPath + " is set to " + ChatColor.translateAlternateColorCodes('&', "&e")
                            + Objects.requireNonNull(Flashlight.getInstance().getMainConfig().get(configPath))
                            + ChatColor.translateAlternateColorCodes('&', "&r")
            );
            return true;
        }

        switch (configPath) {
            case "depth" -> {
                int depth;
                try {
                    depth = Integer.parseInt(configValue);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return false;
                }
                if (depth < 1 || depth > 40) {
                    ChatUtils.error(sender, "Input value must be between 1 and 40.");
                    return false;
                }
                Flashlight.getInstance().getMainConfig().set(configPath, depth);
                ChatUtils.success(sender, configPath + " has been set to " + depth + "!");
            }
            case "degree" -> {
                int degree;
                try {
                    Flashlight.getInstance().getLogger().info(configValue);
                    degree = Integer.parseInt(configValue);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return false;
                }
                if (degree < 10 || degree > 90) {
                    ChatUtils.error(sender, "Input value must be between 10 and 90.");
                    return false;
                }
                Flashlight.getInstance().getMainConfig().set(configPath, degree);
                ChatUtils.success(sender, configPath + " has been set to " + degree + "!");
            }
            case "brightness" -> {
                int brightness;
                try {
                    brightness = Integer.parseInt(configValue);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return false;
                }
                if (brightness < 0 || brightness > 15) {
                    ChatUtils.error(sender, "Input value must be between 0 and 15.");
                    return false;
                }
                Flashlight.getInstance().getMainConfig().set(configPath, brightness);
                ChatUtils.success(sender, configPath + " has been set to " + brightness + "!");
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
