package me.foxyy.events;

import me.foxyy.Flashlight;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Flashlight.getInstance().getPlayerConfig(e.getPlayer().getUniqueId()).set("toggleState", false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Flashlight.getInstance().getPlayerConfig(e.getPlayer().getUniqueId()).set("toggleState", false);
    }
}
