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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlashlightCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command c,
                             @NonNull String label,
                             @NonNull String @NonNull [] args) {
        if (!(sender instanceof Player targetPlayer)) {
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
            // Override the targetPlayer to the specified player
            targetPlayer = Flashlight.getInstance().getServer().getPlayer(args[1]);
            if (targetPlayer == null) {
                ChatUtils.error(sender, "The specified player is not online.");
                return true;
            }
        } else {
            ChatUtils.error(sender, "Incorrect number of arguments!");
            return false;
        }

        boolean state = Flashlight.getInstance().getPlayerConfig(targetPlayer.getUniqueId()).getBoolean("toggleState");

        switch (action) {
            case "on" -> {
                if (state) {
                    ChatUtils.error(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is already &aon&r!"));
                } else {
                    setState(sender, targetPlayer, true);
                }
            }
            case "off" -> {
                if (!state) {
                    ChatUtils.error(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is already &coff&r!"));
                } else {
                    setState(sender, targetPlayer, false);
                }
            }
            case "get" -> {
                if (state) {
                    ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is currently &aon&r!"));
                } else {
                    ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "The flashlight is currently &coff&r!"));
                }
            }
            case "toggle" -> setState(sender, targetPlayer, !state);
            default -> {
                ChatUtils.error(sender, "Unknown subcommand!");
                return false;
            }
        }

        return true;
    }

    private void setState(CommandSender sender, Player targetPlayer, boolean state) {
        if (state) {
            ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "Flashlight is now &aon&r!"));
        } else {
            ChatUtils.sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "Flashlight is now &coff&r!"));
        }
        Flashlight.getInstance().getPlayerConfig(targetPlayer.getUniqueId()).set("toggleState", state);
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      @NonNull String @NonNull [] args) {
        Arrays.setAll(args, i -> args[i].toLowerCase());

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("on");
            list.add("off");
            list.add("get");
            list.add("toggle");
        } else if (args.length == 2) {
            list.add("<player_name>");
        }
        return list;
    }
}
