package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import net.md_5.bungee.api.*;
import io.dhoom.tournament.*;
import io.dhoom.events.*;
import org.bukkit.event.*;
import io.dhoom.player.*;
import java.util.*;
import io.dhoom.duel.*;
import io.dhoom.kit.*;
import io.dhoom.party.*;

public class AcceptCommand implements CommandExecutor
{
    private Practice plugin;
    
    public AcceptCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.accept.unable")));
            return true;
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
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequests(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.no_requests")));
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_not_found")));
            return true;
        }
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequestFromPlayer(player, target)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.no_requests")));
            return true;
        }
        final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
        if (practiceTarget.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_busy")));
            return true;
        }
        final DuelRequest request = this.plugin.getManagerHandler().getRequestManager().getDuelRequest(player, target);
        if (request == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.no_requests")));
            return true;
        }
        this.plugin.getManagerHandler().getRequestManager().removeDuelRequest(player, target);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.accept.accept").replace("%player%", player.getName())));
        final String kitName = request.getKitName();
        final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
        final ArrayList<UUID> firstTeam = new ArrayList<UUID>();
        final ArrayList<UUID> secondTeam = new ArrayList<UUID>();
        firstTeam.add(player.getUniqueId());
        secondTeam.add(target.getUniqueId());
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
        if (party != null && targetParty != null) {
            for (final UUID member : party.getMembers()) {
                firstTeam.add(member);
            }
            for (final UUID member : targetParty.getMembers()) {
                secondTeam.add(member);
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, party.getLeader(), targetParty.getLeader(), firstTeam, secondTeam, false));
        }
        else {
            if ((party != null && targetParty == null) || (targetParty != null && party == null)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.accept.inparty")));
                return true;
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, null, null, firstTeam, secondTeam, false));
        }
        return true;
    }
}
