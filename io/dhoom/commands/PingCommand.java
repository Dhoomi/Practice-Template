package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.apache.commons.lang.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;

public class PingCommand implements CommandExecutor
{
    private Practice plugin;
    
    public PingCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        Player toCheck;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /ping <player>");
                return true;
            }
            toCheck = (Player)sender;
        }
        else {
            toCheck = Bukkit.getPlayer(StringUtils.join((Object[])args));
        }
        if (toCheck == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.ping.notfound").replace("%name%", StringUtils.join((Object[])args))));
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.ping.ping").replace("%name%", toCheck.getName())).replace("%s%", toCheck.getName().endsWith("s") ? "'" : "'s").replace("%ping%", String.valueOf(this.getPing(toCheck))));
        if (sender instanceof Player && !toCheck.getName().equals(sender.getName())) {
            final Player senderPlayer = (Player)sender;
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.ping.difference").replace("%difference%", Math.max(this.getPing(senderPlayer), this.getPing(toCheck)) - Math.min(this.getPing(senderPlayer), this.getPing(toCheck)) + "")));
        }
        return true;
    }
    
    private int getPing(final Player player) {
        final int ping = ((CraftPlayer)player).getHandle().ping;
        if (ping >= 100) {
            return ping - 30;
        }
        if (ping >= 50) {
            return ping - 20;
        }
        if (ping >= 20) {
            return ping - 10;
        }
        return ping;
    }
}
