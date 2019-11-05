package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import net.md_5.bungee.api.*;
import io.dhoom.arena.*;

public class ArenaCommand implements CommandExecutor
{
    private Practice plugin;
    
    public ArenaCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] commandArgs) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (commandArgs.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7*** &cArena Help &7***"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena create &7- creates an arena"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena setpos1 &7- set spawn position #1"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena setpos2 &7- set sapwn position #2"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena enable &7- enable the arena for use"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena disable &7- disable the arena for use"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena remove &7- remove the arena completely"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena sumo &7- set the arena as sumo-only"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena builduhc &7- set the arena as builuhc-only"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena combo &7- set the arena as combo-only"));
            return true;
        }
        if (!player.hasPermission("practice.commands.arena")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.nopermission")));
            return true;
        }
        if (commandArgs[0].equalsIgnoreCase("create")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena create <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            if (this.plugin.getManagerHandler().getArenaManager().getArena(arenaName) != null) {
                player.sendMessage(ChatColor.RED + "This arena already exists!");
                return true;
            }
            this.plugin.getManagerHandler().getArenaManager().createArena(arenaName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.arena.create").replace("%arena%", arenaName)));
        }
        else if (commandArgs[0].equalsIgnoreCase("setpos1")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena setpos1 <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setFirstTeamLocation(player.getLocation());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.arena.pos1").replace("%arena%", arenaName)));
        }
        else if (commandArgs[0].equalsIgnoreCase("setpos2")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena setpos2 <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setSecondTeamLocation(player.getLocation());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.arena.pos2").replace("%arena%", arenaName)));
        }
        else if (commandArgs[0].equalsIgnoreCase("enable") || commandArgs[1].equalsIgnoreCase("disable")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena enable <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setEnabled(!arena.isEnabled());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.arena.toggle").replace("%arena%", arenaName).replace("%toggle%", arena.isEnabled() ? "enabled" : "disabled")));
        }
        else if (commandArgs[0].equalsIgnoreCase("remove")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena remove <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            if (this.plugin.getManagerHandler().getArenaManager().getArena(arenaName) == null) {
                player.sendMessage(ChatColor.RED + "This arena does not exist!");
                return true;
            }
            this.plugin.getManagerHandler().getArenaManager().destroyArena(arenaName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.arena.remove").replace("%arena%", arenaName)));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7*** &cArena Help &7***"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena create &7- creates an arena"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena setpos1 &7- set spawn position #1"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena setpos2 &7- set sapwn position #2"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena enable &7- enable the arena for use"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena disable &7- disable the arena for use"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena remove &7- remove the arena completely"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena sumo &7- set the arena as sumo-only"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena builduhc &7- set the arena as builuhc-only"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/arena combo &7- set the arena as combo-only"));
        }
        return true;
    }
}
