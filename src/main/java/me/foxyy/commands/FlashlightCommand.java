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
        if (!Flashlight.getInstance().flashlightState.get((Player) sender)) {
            ChatUtils.sendPrefixMessage((Player)sender, ChatColor.translateAlternateColorCodes('&', "Flashlight is now &aon&r!"));
        } else {
            ChatUtils.sendPrefixMessage((Player)sender, ChatColor.translateAlternateColorCodes('&', "Flashlight is now &coff&r!"));
        }
        Flashlight.getInstance().flashlightState.compute((Player) sender, (Player player, Boolean state) -> Boolean.FALSE.equals(state));
        return true;
    }
}
