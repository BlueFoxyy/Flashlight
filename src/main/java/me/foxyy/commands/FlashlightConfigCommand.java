package me.foxyy.commands;

import me.foxyy.Flashlight;
import me.foxyy.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;

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

        if (args.length <= 1 || args.length > 3) {
            return false;
        }

        String target = args[0];
        String configField = args[1].toLowerCase();

        Player player = Flashlight.getInstance().getServer().getPlayer(target);
        if (player == null) {
            ChatUtils.error(sender, "The specified player is not online.");
            return true;
        }
        UUID playerUUID = player.getUniqueId();

        if (!parameters.contains(configField)) {
            return false;
        }

        String configValue;
        if (args.length == 3) {
            configValue = args[2];
        } else {
            // query
            ChatUtils.sendPrefixMessage(sender,
                    configField + " is set to " + ChatColor.translateAlternateColorCodes('&', "&e")
                            + Objects.requireNonNull(Flashlight.getInstance().getPlayerConfig(playerUUID).get(configField))
                            + ChatColor.translateAlternateColorCodes('&', "&r")
            );
            return true;
        }

        switch (configField) {
            case "depth" -> {
                int depth;
                try {
                    depth = Integer.parseInt(configValue);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return true;
                }
                if (depth < 1 || depth > 40) {
                    ChatUtils.error(sender, "Input value must be between 1 and 40.");
                    return true;
                }
                Flashlight.getInstance().getPlayerConfig(playerUUID).set(configField, depth);
                ChatUtils.success(sender, configField + " has been set to " + depth + "!");
            }
            case "degree" -> {
                int degree;
                try {
                    Flashlight.getInstance().getLogger().info(configValue);
                    degree = Integer.parseInt(configValue);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return true;
                }
                if (degree < 10 || degree > 90) {
                    ChatUtils.error(sender, "Input value must be between 10 and 90.");
                    return true;
                }
                Flashlight.getInstance().getPlayerConfig(playerUUID).set(configField, degree);
                ChatUtils.success(sender, configField + " has been set to " + degree + "!");
            }
            case "brightness" -> {
                int brightness;
                try {
                    brightness = Integer.parseInt(configValue);
                } catch (NumberFormatException e) {
                    ChatUtils.error(sender, "Input value must be an integer.");
                    return true;
                }
                if (brightness < 0 || brightness > 15) {
                    ChatUtils.error(sender, "Input value must be between 0 and 15.");
                    return true;
                }
                Flashlight.getInstance().getPlayerConfig(playerUUID).set(configField, brightness);
                ChatUtils.success(sender, configField + " has been set to " + brightness + "!");
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
            list.add("<player_name>");
        } else if (args.length == 2) {
            list = parameters;
        } else if (args.length == 3 && parameters.contains(args[1])) {
            list.add("[value]");
        }
        return list;
    }
}
