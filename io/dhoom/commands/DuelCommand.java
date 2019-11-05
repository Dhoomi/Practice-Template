package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import io.kipes.tournament.*;
import io.dhoom.player.*;
import io.dhoom.party.*;
import java.util.*;

public class DuelCommand implements CommandExecutor
{
    private Practice plugin;
    
    public DuelCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
            return false;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (!tournament.isInTournament(player)) {
                    continue;
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
                return true;
            }
        }
        final Player target;
        if ((target = this.plugin.getServer().getPlayer(args[0])) == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_not_found")));
            return true;
        }
        if (target.getName().equals(player.getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.selfduel")));
            return true;
        }
        final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
        if (practiceTarget.getCurrentState() != PlayerState.LOBBY || !practiceTarget.getSettings().isDuelRequests()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_busy")));
            return true;
        }
        if (this.plugin.getManagerHandler().getRequestManager().hasDuelRequests(target) && this.plugin.getManagerHandler().getRequestManager().hasDuelRequestFromPlayer(target, player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.waiting")));
            return true;
        }
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
        if (party != null) {
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.duel.not_leader")));
                return true;
            }
            if (targetParty == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.duel.not_in_party")));
                return true;
            }
            if (!targetParty.getLeader().equals(target.getUniqueId())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.duel.player_not_leader")));
                return true;
            }
            if (targetParty.getPartyState() == PartyState.DUELING) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.duel.party_busy")));
                return true;
            }
        }
        else if (targetParty != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.duel.player_in_party")));
            return true;
        }
        final UUID uuid = player.getUniqueId();
        this.plugin.getManagerHandler().getInventoryManager().setSelectingDuel(uuid, target.getUniqueId());
        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRequestInventory());
        return true;
    }
}
