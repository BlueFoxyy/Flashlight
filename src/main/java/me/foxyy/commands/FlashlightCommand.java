package me.foxyy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FlashlightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command c,
                             @NonNull String label,
                             @NonNull String[] args) {
        return false;
    }
}
