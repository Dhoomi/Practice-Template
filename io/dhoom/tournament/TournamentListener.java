package io.dhoom.tournament;

import org.bukkit.event.player.*;
import org.bukkit.*;
import io.dhoom.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.*;

public class TournamentListener implements Listener
{
    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (Tournament.getTournaments().size() == 0) {
            return;
        }
        for (final Tournament tournament : Tournament.getTournaments()) {
            if (tournament.isInTournament(player)) {
                final TournamentTeam tournamentTeam;
                if ((tournamentTeam = tournament.getTournamentTeam(player)) == null) {
                    continue;
                }
                final String reason = (tournamentTeam.getPlayers().size() > 1) ? "(Tournament) Someone in your party left the server" : "(Tournament) You have left the server";
                tournamentTeam.sendMessage(ChatColor.RED + reason);
                final TournamentMatch match = tournament.getTournamentMatch(player);
                if (match == null) {
                    continue;
                }
                Practice.getInstance().getManagerHandler().getDuelManager().removePlayerFromDuel(player);
            }
        }
    }
}
