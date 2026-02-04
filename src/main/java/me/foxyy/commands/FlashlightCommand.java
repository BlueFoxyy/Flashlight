package me.foxyy.commands;

import me.foxyy.Flashlight;
import me.foxyy.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FlashlightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command c,
                             @NonNull String label,
                             @NonNull String @NonNull [] args) {
        if (!(sender instanceof Player target)) {
            ChatUtils.error(sender, "This command can only be used by players!");
            return false;
        }

        String action;
        if (args.length == 0) {
            action = "toggle";
        } else if (args.length == 1) {
            action = args[0];
        } else if (args.length == 2) {
            action = args[0];
            // Override the target to the specified player
            target = Flashlight.getInstance().getServer().getPlayer(args[1]);
        } else {
            ChatUtils.error(sender, "Incorrect number of arguments!");
            return false;
        }

        boolean state = Flashlight.getInstance().flashlightState.get(target);

        switch (action) {
            case "on" -> {
                if (state) {
                    ChatUtils.error(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is already &aon&r!"));
                } else {
                    setState(sender, target, true);
                }
            }
            case "off" -> {
                if (!state) {
                    ChatUtils.error(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is already &aon&r!"));
                } else {
                    setState(sender, target, false);
                }
            }
            case "get" -> {
                if (state) {
                    ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is currently &aon&r!"));
                } else {
                    ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is currently &coff&r!"));
                }
            }
            case "toggle" -> setState(sender, target, !state);
            default -> {
                ChatUtils.error(sender, "Unknown subcommand!");
                return false;
            }
        }

        return true;
    }

    private void setState(CommandSender sender, Player target, boolean state) {
        if (state) {
            ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "Flashlight is now &aon&r!"));
        } else {
            ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "Flashlight is now &coff&r!"));
        }
        Flashlight.getInstance().flashlightState.put(target, state);
    }
}
