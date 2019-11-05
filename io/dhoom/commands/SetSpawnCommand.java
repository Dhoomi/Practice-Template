package io.dhoom.commands;

import org.bukkit.event.*;
import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import io.dhoom.util.*;

public class SetSpawnCommand implements CommandExecutor, Listener
{
    private Practice plugin;
    
    public SetSpawnCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.nopermission")));
            return true;
        }
        this.plugin.getConfig().set("spawn", (Object)LocationSerializer.serializeLocation(player.getLocation()));
        this.plugin.saveConfig();
        this.plugin.reloadConfig();
        this.plugin.setSpawn(player.getLocation());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.spawn.set")));
        return true;
    }
}
