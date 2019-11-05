package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import net.md_5.bungee.api.*;
import org.bukkit.*;
import io.dhoom.player.*;

public class AdminCommand implements CommandExecutor
{
    private Practice plugin;
    
    public AdminCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.commands.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.nopermission")));
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.admin.disabled")));
            return true;
        }
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
            return true;
        }
        practicePlayer.setCurrentState(PlayerState.BUILDER);
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.admin.enabled")));
        return true;
    }
}
