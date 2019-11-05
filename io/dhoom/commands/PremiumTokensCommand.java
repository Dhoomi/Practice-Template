package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import io.dhoom.player.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;

public class PremiumTokensCommand implements CommandExecutor
{
    private Practice plugin;
    
    public PremiumTokensCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.tokens.admin")) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("Incorrect Usage.");
            return true;
        }
        if (args.length == 2) {
            String name = "";
            final Player player = Bukkit.getPlayer(args[0]);
            UUID uuid;
            if (player != null) {
                uuid = player.getUniqueId();
                name = player.getName();
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
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    practicePlayer.setRankPremiumTokens(amount);
                    practicePlayer.save();
                }
            });
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + amount + ChatColor.YELLOW + " tokens to " + ChatColor.GREEN + name + ChatColor.YELLOW + ".");
        }
        else {
            sender.sendMessage(ChatColor.RED + "Incorrect Usage.");
        }
        return true;
    }
}
