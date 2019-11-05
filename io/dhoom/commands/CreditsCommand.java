package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import io.dhoom.player.*;
import org.bukkit.plugin.*;
import java.util.*;

public class CreditsCommand implements CommandExecutor
{
    private Practice plugin;
    
    public CreditsCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.credits.admin")) {
            return true;
        }
        if (args.length == 0) {
            final Player player = (Player)sender;
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.incorrect_usage")));
            return true;
        }
        if (args.length == 2) {
            String name = "";
            final Player player2 = Bukkit.getPlayer(args[0]);
            UUID uuid;
            if (player2 != null) {
                uuid = player2.getUniqueId();
                name = player2.getName();
            }
            else {
                try {
                    final Map.Entry<UUID, String> recipient = PracticePlayer.getExternalPlayerInformation(args[0]);
                    uuid = recipient.getKey();
                    name = recipient.getValue();
                }
                catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Failed to find player.");
                    return true;
                }
            }
            final int amount = Integer.parseInt(args[1]);
            final PracticePlayer practicePlayer = PracticePlayer.getByUuid(uuid);
            if (practicePlayer == null) {
                return true;
            }
            final PracticePlayer practicePlayer2;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
                practicePlayer2.setCredits(practicePlayer2.getCredits() + amount);
                practicePlayer2.save();
                return;
            });
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.credits.recieve").replace("%amount%", String.valueOf(amount))));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.credits.send").replace("%player%", player2.getName()).replace("%amount%", String.valueOf(amount))));
        }
        else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.incorrect_usage")));
        }
        return true;
    }
}
