package me.foxyy.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils {
    public static void sendPrefixMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[Flashlight]&r ") + message);
    }
    public static void info(CommandSender sender, String message) {
        sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "&f" + message + "&r"));
    }
    public static void error(CommandSender sender, String message) {
        sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "&c" + message + "&r"));
    }
    public static void success(CommandSender sender, String message) {
        sendPrefixMessage(sender, ChatColor.translateAlternateColorCodes('&', "&a" + message + "&r"));
    }
}
