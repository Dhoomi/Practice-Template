package io.dhoom.scoreboard;

import org.bukkit.entity.*;
import io.dhoom.*;
import io.dhoom.listeners.*;
import java.text.*;
import io.dhoom.player.*;
import io.dhoom.scoreboard.config.*;
import com.google.common.collect.*;
import io.dhoom.tournament.*;
import io.dhoom.duel.*;
import java.util.*;
import org.apache.commons.lang.time.*;
import org.bukkit.*;
import com.google.common.base.*;

public abstract class SidebarProvider
{
    protected static final String STRAIGHT_LINE;
    public static String SCOREBOARD_TITLE;
    
    public abstract String getTitle(final Player p0);
    
    public abstract List<SidebarEntry> getLines(final Player p0);
    
    public List<SidebarEntry> preprocess(final Player player, final List<String> list) {
        final PracticePlayer practicePlayer = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        final Duel duel = Practice.getInstance().getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId());
        final ArrayList<SidebarEntry> copy = new ArrayList<SidebarEntry>();
        for (String line : list) {
            if (duel != null) {
                if (line.contains("%enderpearl%")) {
                    if (!PlayerListener.getLastPearl().containsKey(player.getUniqueId())) {
                        continue;
                    }
                    final long now;
                    final double diff;
                    final double result;
                    if ((result = 15.0 - (diff = (now = System.currentTimeMillis()) - PlayerListener.getLastPearl().get(player.getUniqueId())) / 1000.0) <= 0.0) {
                        continue;
                    }
                    line = line.replace("%enderpearl%", new DecimalFormat("#0.0").format(result) + "s");
                }
                if (line.contains("%opponent%")) {
                    if (duel.getFfaPlayers() != null && duel.getFfaPlayers().size() > 0) {
                        continue;
                    }
                    final boolean bl;
                    final boolean isParty = bl = (duel.getOtherDuelTeam(player).size() >= 2);
                    final String opponent = (duel.getOtherDuelTeam(player).get(0) != null) ? Bukkit.getOfflinePlayer((UUID)duel.getOtherDuelTeam(player).get(0)).getName() : ("Undefined" + (isParty ? "'s Party" : ""));
                    line = line.replace("%opponent%", opponent);
                }
                line = line.replace("%time_left%", this.getRemaining((duel.getEndMatchTime() != 0L) ? duel.getFinalDuration() : duel.getStartDuration()));
            }
            line = line.replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()));
            line = line.replace("%in_fights%", String.valueOf(this.getTotalInGame()));
            line = line.replace("%in_queue%", String.valueOf(Practice.getInstance().getManagerHandler().getQueueManager().getTotalInQueues()));
            if ((line = line.replace("%premium_matches%", String.valueOf(PracticePlayer.getByUuid(player.getUniqueId()).getPremiumTokens()))).contains("%tournament%")) {
                final List<String> torunamentLines = Practice.getInstance().getScoreboardConfig().get(ScoreboardType.TOURNAMENT);
                if (Tournament.getTournaments() == null) {
                    continue;
                }
                if (Tournament.getTournaments().isEmpty()) {
                    continue;
                }
                final Tournament tournament = Tournament.getTournaments().get(0);
                final HashSet counted = Sets.newHashSet();
                for (final TournamentMatch tournamentMatch : tournament.getCurrentMatches()) {
                    counted.addAll(tournamentMatch.getMatchPlayers());
                }
                for (final TournamentTeam tournamentTeam : tournament.getCurrentQueue()) {
                    counted.addAll(tournamentTeam.getPlayers());
                }
                for (final TournamentTeam tournamentTeam : tournament.getTeams()) {
                    counted.addAll(tournamentTeam.getPlayers());
                }
                for (String tournamentLine : torunamentLines) {
                    tournamentLine = tournamentLine.replace("%tournament_stage%", (tournament.getTournamentStage() == null) ? "Uncommenced" : tournament.getTournamentStage().toReadable());
                    tournamentLine = tournamentLine.replace("%tournament_players_total%", String.valueOf(counted.size()));
                    copy.add(new SidebarEntry(tournamentLine));
                }
            }
            else {
                copy.add(new SidebarEntry(line));
            }
        }
        return copy;
    }
    
    private int getTotalInGame() {
        int count = 0;
        for (final Duel duel : kPractice.getInstance().getManagerHandler().getDuelManager().getUuidIdentifierToDuel().values()) {
            if (duel.getFirstTeam() != null) {
                count += duel.getFirstTeam().size();
            }
            if (duel.getSecondTeam() != null) {
                count += duel.getSecondTeam().size();
            }
            if (duel.getFfaPlayers() == null) {
                continue;
            }
            count += duel.getFfaPlayers().size();
        }
        return count;
    }
    
    private String getRemaining(final long duration) {
        return DurationFormatUtils.formatDuration(duration, "mm:ss");
    }
    
    static {
        STRAIGHT_LINE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256).substring(0, 10);
    }
}
