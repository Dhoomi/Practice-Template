package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import io.dhoom.lang.*;
import io.dhoom.player.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;

public class MatchesCommand implements CommandExecutor
{
    private Practice plugin;
    
    public MatchesCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.matches.admin")) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "INCORRECT_USEAGE"));
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
            final PracticePlayer practicePlayer2;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
                practicePlayer2.setPremiumTokens(practicePlayer2.getPremiumTokens() + amount);
                practicePlayer2.save();
                return;
            });
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "GIVEN_PREMIUM_MATCHES").replace("%amount%", String.valueOf(amount)).replace("%player%", player.getName()));
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                sender.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "RECEIVED_PREMIUM_MATCHES").replace("%amount%", String.valueOf(amount)));
            }
        }
        else {
            final Player player2 = (Player)sender;
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "INCORRECT_USAGE"));
        }
        return true;
    }
}
