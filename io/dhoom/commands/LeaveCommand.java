package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import io.dhoom.lang.*;
import org.bukkit.*;
import io.dhoom.runnables.other.*;
import org.bukkit.plugin.*;
import io.dhoom.player.*;
import io.dhoom.tournament.*;

public class LeaveCommand implements CommandExecutor
{
    private Practice plugin;
    
    public LeaveCommand(final Practice plugin) {
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
        if (args.length == 0) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "NO_TOURNAMENT"));
                return true;
            }
            final Tournament tournament = Tournament.getTournaments().get(0);
            if (tournament.getTotalPlayersInTournament() == tournament.getPlayersLimit()) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "FULL_TOURNAMENT"));
                return true;
            }
            if (!tournament.isInTournament(player)) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "NOT_IN_TOURNAMENT"));
                return true;
            }
            final TournamentTeam tournamentTeam = tournament.getTournamentTeam(player);
            if (tournamentTeam == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "NOT_IN_TOURNAMENT"));
                return true;
            }
            if (tournament.getTournamentMatch(player) != null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "CANNOT_LEAVE_IN_MATCH"));
                player.sendMessage(ChatColor.RED + "You can't leave during a match.");
                return true;
            }
            final String reason = (tournamentTeam.getPlayers().size() > 1) ? "Someone in your party left the tournament" : "You left the tournament";
            tournamentTeam.sendMessage(ChatColor.RED + "You have been removed from the tournament.");
            tournamentTeam.sendMessage(ChatColor.RED + "Reason: " + ChatColor.GRAY + reason);
            tournament.getCurrentQueue().remove(tournamentTeam);
            tournament.getTeams().remove(tournamentTeam);
            this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
        }
        return true;
    }
}
