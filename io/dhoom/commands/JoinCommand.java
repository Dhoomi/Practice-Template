package io.kipes.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import io.dhoom.tournament.*;
import io.dhoom.runnables.other.*;
import org.bukkit.plugin.*;
import io.dhoom.player.*;
import java.util.*;
import io.dhoom.party.*;

public class JoinCommand implements CommandExecutor
{
    private Practice plugin;
    
    public JoinCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
            return true;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (tournament.isInTournament(player)) {
                    player.sendMessage(ChatColor.RED + "You are already in a tournament.");
                    return true;
                }
            }
        }
        if (args.length == 0) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(ChatColor.RED + "There are no tournaments available.");
                return true;
            }
            player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getJoinTournamentInventory());
        }
        else if (args.length == 1) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(ChatColor.RED + "There are no tournaments available.");
                return true;
            }
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (tournament.isInTournament(player)) {
                    player.sendMessage(ChatColor.RED + "You are currently in another tournament.");
                    return true;
                }
            }
            final int id = Integer.parseInt(args[0]);
            Tournament tournament = Tournament.getTournaments().get(id - 1);
            if (tournament == null) {
                player.sendMessage(ChatColor.RED + "That tournament id doesn't exist.");
                return true;
            }
            if (tournament.isStarted()) {
                player.sendMessage(ChatColor.RED + "Sorry! The tournament already started.");
                return true;
            }
            if (tournament.getTotalPlayersInTournament() == tournament.getPlayersLimit()) {
                player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
                return true;
            }
            if (tournament.isInTournament(player)) {
                player.sendMessage(ChatColor.RED + "You are already in the tournament.");
                return true;
            }
            if (tournament.getMaximumPerTeam() == 1) {
                final TournamentTeam tournamentTeam = new TournamentTeam();
                tournamentTeam.setPlayers(Collections.singletonList(player.getUniqueId()));
                tournament.getTeams().add(tournamentTeam);
                tournament.sendMessage(ChatColor.YELLOW + player.getName() + " has joined the tournament. (" + tournament.getTotalPlayersInTournament() + "/" + tournament.getPlayersLimit() + ")");
            }
            else if (tournament.getMaximumPerTeam() >= 2) {
                final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "You must be in a party to join this tournament.");
                    return true;
                }
                if (party.getLeader() != player.getUniqueId()) {
                    player.sendMessage(ChatColor.RED + "Only the leader can join the tournament.");
                    return true;
                }
                if (party.getSize() != tournament.getMaximumPerTeam()) {
                    player.sendMessage(ChatColor.RED + "The party must have only " + tournament.getMaximumPerTeam() + " players.");
                    return true;
                }
                final TournamentTeam tournamentTeam2 = new TournamentTeam();
                tournamentTeam2.setPlayers(party.getAllMembersOnline());
                tournament.getTeams().add(tournamentTeam2);
                tournament.sendMessage(ChatColor.YELLOW + player.getName() + "'s Party has joined the tournament. (" + tournament.getTotalPlayersInTournament() + "/" + tournament.getPlayersLimit() + ")");
                this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Usage: /join");
        }
        return true;
    }
}
