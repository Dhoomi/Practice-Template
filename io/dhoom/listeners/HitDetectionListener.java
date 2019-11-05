package io.dhoom.listeners;

import io.dhoom.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

public class HitDetectionListener implements Listener
{
    private final Practice plugin;
    
    public HitDetectionListener(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Practice.getInstance());
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.setMaximumNoDamageTicks(19);
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        event.getPlayer().setMaximumNoDamageTicks(19);
    }
}
