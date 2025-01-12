package me.foxyy.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static void sendPrefixMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[Flashlight]&r ") + message);
    }
    public static void info(Player player, String message) {
        sendPrefixMessage(player, ChatColor.translateAlternateColorCodes('&', "&f" + message + "&r"));
    }
    public static void error(Player player, String message) {
        sendPrefixMessage(player, ChatColor.translateAlternateColorCodes('&', "&c" + message + "&r"));
    }
    public static void success(Player player, String message) {
        sendPrefixMessage(player, ChatColor.translateAlternateColorCodes('&', "&a" + message + "&r"));
    }
}
