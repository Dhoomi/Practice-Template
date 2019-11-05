package io.dhoom.tournament;

import io.dhoom.*;
import io.dhoom.kit.*;
import io.dhoom.tournament.tasks.*;
import org.bukkit.plugin.*;
import org.bukkit.scheduler.*;
import io.dhoom.runnables.other.*;
import io.dhoom.lang.*;
import org.bukkit.command.*;
import org.bukkit.*;
import io.dhoom.commands.*;
import org.bukkit.entity.*;
import java.util.*;

public class Tournament
{
    private static List<Tournament> tournaments;
    private Practice plugin;
    private int maximumPerTeam;
    private int playersLimit;
    private TournamentStage tournamentStage;
    private TournamentTeam luckyTeam;
    private List<TournamentTeam> teams;
    private List<TournamentMatch> currentMatches;
    private HashMap<TournamentStage, List<TournamentMatch>> matchesHistory;
    private List<TournamentTeam> currentQueue;
    private Kit defaultKit;
    private int taskId;
    private boolean playerTournament;
    private Long timeoutIdle;
    private boolean started;
    private boolean forceStarted;
    
    public Tournament(final int maximumPerTeam, final Kit defaultKit, final boolean playerTournament) {
        this.plugin = Practice.getInstance();
        this.maximumPerTeam = maximumPerTeam;
        this.defaultKit = defaultKit;
        this.teams = new ArrayList<TournamentTeam>();
        this.currentMatches = new ArrayList<TournamentMatch>();
        this.currentQueue = new ArrayList<TournamentTeam>();
        this.matchesHistory = new HashMap<TournamentStage, List<TournamentMatch>>();
        this.forceStarted = false;
        this.started = false;
        this.playerTournament = playerTournament;
        this.playersLimit = (this.playerTournament ? 16 : 32) * this.maximumPerTeam;
        this.timeoutIdle = (this.playerTournament ? (System.currentTimeMillis() + 900000L) : 0L);
        Tournament.tournaments.add(this);
        this.taskId = this.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin, (BukkitRunnable)new TournamentTask(this), 200L, 200L).getTaskId();
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
    }
    
    public static void forceEndPlayerTournaments() {
        if (Tournament.tournaments.size() == 0) {
            return;
        }
        final Iterator<Tournament> iterator = Tournament.tournaments.iterator();
        while (iterator.hasNext()) {
            final Tournament tournament = iterator.next();
            if (tournament != null) {
                if (!tournament.isPlayerTournament()) {
                    continue;
                }
                Bukkit.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_FORCE_STOP").replace("%max_per_team%", String.valueOf(tournament.maximumPerTeam)));
                if (tournament.getCurrentMatches().size() > 0) {
                    for (final TournamentMatch tournamentMatch : tournament.getCurrentMatches()) {
                        for (final UUID uuid : tournamentMatch.getMatchPlayers()) {
                            final Player player = Bukkit.getPlayer(uuid);
                            if (player == null) {
                                continue;
                            }
                            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "(-) You are still able to continue fighting.");
                        }
                    }
                }
                if (HostCommand.getRunningTournaments().size() >= 1) {
                    HostCommand.getRunningTournaments().clear();
                }
                Bukkit.getServer().getScheduler().cancelTask(tournament.taskId);
                iterator.remove();
            }
        }
    }
    
    public static List<Tournament> getTournaments() {
        return Tournament.tournaments;
    }
    
    public void stopTournament() {
        this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_FORCE_STOP").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)));
        if (this.currentMatches.size() > 0) {
            for (final TournamentMatch tournamentMatch : this.currentMatches) {
                for (final UUID uuid : tournamentMatch.getMatchPlayers()) {
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        continue;
                    }
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "(-) You are still able to continue fighting.");
                }
            }
        }
        if (HostCommand.getRunningTournaments().size() >= 1) {
            HostCommand.getRunningTournaments().clear();
        }
        this.plugin.getServer().getScheduler().cancelTask(this.taskId);
        Tournament.tournaments.remove(this);
    }
    
    public void generateRoundMatches() {
        if (this.timeoutIdle != 0L && this.timeoutIdle < System.currentTimeMillis()) {
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_ENDED_TIMEOUT").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)));
            this.stopTournament();
            return;
        }
        if (this.getTotalPlayersInTournament() < this.playersLimit && !this.forceStarted && !this.started) {
            return;
        }
        if (this.currentMatches.size() > 0 && this.teams.size() > 1) {
            return;
        }
        if (this.isMatchOnGoing()) {
            return;
        }
        if (this.teams.size() <= 64 && this.teams.size() > 32 && this.tournamentStage != TournamentStage.FIRST_ROUND) {
            this.setTournamentStage(TournamentStage.FIRST_ROUND);
            if (!this.started) {
                this.started = true;
            }
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_ROUNDS").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)).replace("%round%", "First"));
        }
        else if (this.teams.size() <= 32 && this.teams.size() > 16 && this.tournamentStage != TournamentStage.SECOND_ROUND) {
            this.setTournamentStage(TournamentStage.SECOND_ROUND);
            if (!this.started) {
                this.started = true;
            }
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_ROUNDS").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)).replace("%round%", "Second"));
        }
        else if (this.teams.size() <= 16 && this.teams.size() > 8 && this.tournamentStage != TournamentStage.THIRD_ROUND) {
            this.setTournamentStage(TournamentStage.THIRD_ROUND);
            if (!this.started) {
                this.started = true;
            }
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_ROUNDS").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)).replace("%round%", "Third"));
        }
        else if (this.teams.size() <= 8 && this.teams.size() > 4 && this.tournamentStage != TournamentStage.QUARTER_FINALS) {
            this.setTournamentStage(TournamentStage.QUARTER_FINALS);
            if (!this.started) {
                this.started = true;
            }
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_ROUNDS").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)).replace("%round%", "Quarter Finals"));
        }
        else if (this.teams.size() <= 4 && this.teams.size() > 2 && this.tournamentStage != TournamentStage.SEMI_FINALS) {
            this.setTournamentStage(TournamentStage.SEMI_FINALS);
            if (!this.started) {
                this.started = true;
            }
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_ROUNDS").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)).replace("%round%", "Semi-Finals"));
        }
        else if (this.teams.size() == 2 && this.tournamentStage != TournamentStage.FINALS) {
            this.setTournamentStage(TournamentStage.FINALS);
            if (!this.started) {
                this.started = true;
            }
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_ROUNDS").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)).replace("%round%", "Grand-Final"));
        }
        else if (this.teams.size() == 1) {
            final StringJoiner members = new StringJoiner(", ");
            for (final UUID uuid : this.teams.get(0).getPlayers()) {
                if (Bukkit.getOfflinePlayer(uuid) == null) {
                    continue;
                }
                members.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
            this.plugin.getServer().broadcastMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_WINNERS").replace("%max_per_team%", String.valueOf(this.maximumPerTeam)).replace("%winners%", members.toString()));
            this.plugin.getServer().getScheduler().cancelTask(this.taskId);
            Tournament.tournaments.remove(this);
            return;
        }
        this.currentQueue.clear();
        this.currentQueue.addAll(this.teams);
        if (this.currentQueue.size() % 2 != 0) {
            this.luckyTeam = this.currentQueue.get(0);
            this.currentQueue.remove(0);
            this.luckyTeam.sendMessage(ChatColor.YELLOW + "You have been skipped to the next round.");
            this.luckyTeam.sendMessage(ChatColor.YELLOW + "There was no matching team for you.");
        }
        this.currentMatches.clear();
        final Iterator<TournamentTeam> tournamentTeamIterator = this.currentQueue.iterator();
        while (tournamentTeamIterator.hasNext()) {
            final TournamentMatch match = new TournamentMatch();
            match.setMatchState(TournamentMatch.MatchState.WAITING);
            match.setFirstTeam(tournamentTeamIterator.next());
            match.setSecondTeam(tournamentTeamIterator.next());
            match.start(this.plugin, this.defaultKit);
            this.currentMatches.add(match);
        }
        this.matchesHistory.put(this.tournamentStage, this.currentMatches);
    }
    
    public void sendMessage(final String message) {
        for (final TournamentTeam team : this.teams) {
            team.sendMessage(message);
        }
    }
    
    public boolean isInTournament(final Player player) {
        for (final TournamentTeam tournamentTeam : this.teams) {
            if (!tournamentTeam.getPlayers().contains(player.getUniqueId())) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    public TournamentMatch getTournamentMatch(final Player player) {
        for (final TournamentMatch match : this.currentMatches) {
            if (!match.getMatchPlayers().contains(player.getUniqueId())) {
                continue;
            }
            return match;
        }
        return null;
    }
    
    private boolean isMatchOnGoing() {
        for (final TournamentMatch match : this.currentMatches) {
            if (match.getMatchState() != TournamentMatch.MatchState.FIGHTING) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    public int getTotalPlayersInTournament() {
        int total = 0;
        for (final TournamentTeam tournamentTeam : this.teams) {
            total += tournamentTeam.getPlayers().size();
        }
        return total;
    }
    
    public TournamentTeam getTournamentTeam(final Player player) {
        for (final TournamentTeam tournamentTeam : this.teams) {
            if (!tournamentTeam.getPlayers().contains(player.getUniqueId())) {
                continue;
            }
            return tournamentTeam;
        }
        return null;
    }
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public int getMaximumPerTeam() {
        return this.maximumPerTeam;
    }
    
    public void setMaximumPerTeam(final int maximumPerTeam) {
        this.maximumPerTeam = maximumPerTeam;
    }
    
    public int getPlayersLimit() {
        return this.playersLimit;
    }
    
    public void setPlayersLimit(final int playersLimit) {
        this.playersLimit = playersLimit;
    }
    
    public TournamentStage getTournamentStage() {
        return this.tournamentStage;
    }
    
    public void setTournamentStage(final TournamentStage tournamentStage) {
        this.tournamentStage = tournamentStage;
    }
    
    public TournamentTeam getLuckyTeam() {
        return this.luckyTeam;
    }
    
    public void setLuckyTeam(final TournamentTeam luckyTeam) {
        this.luckyTeam = luckyTeam;
    }
    
    public List<TournamentTeam> getTeams() {
        return this.teams;
    }
    
    public void setTeams(final List<TournamentTeam> teams) {
        this.teams = teams;
    }
    
    public List<TournamentMatch> getCurrentMatches() {
        return this.currentMatches;
    }
    
    public void setCurrentMatches(final List<TournamentMatch> currentMatches) {
        this.currentMatches = currentMatches;
    }
    
    public HashMap<TournamentStage, List<TournamentMatch>> getMatchesHistory() {
        return this.matchesHistory;
    }
    
    public void setMatchesHistory(final HashMap<TournamentStage, List<TournamentMatch>> matchesHistory) {
        this.matchesHistory = matchesHistory;
    }
    
    public List<TournamentTeam> getCurrentQueue() {
        return this.currentQueue;
    }
    
    public void setCurrentQueue(final List<TournamentTeam> currentQueue) {
        this.currentQueue = currentQueue;
    }
    
    public Kit getDefaultKit() {
        return this.defaultKit;
    }
    
    public void setDefaultKit(final Kit defaultKit) {
        this.defaultKit = defaultKit;
    }
    
    public int getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final int taskId) {
        this.taskId = taskId;
    }
    
    public boolean isPlayerTournament() {
        return this.playerTournament;
    }
    
    public void setPlayerTournament(final boolean playerTournament) {
        this.playerTournament = playerTournament;
    }
    
    public Long getTimeoutIdle() {
        return this.timeoutIdle;
    }
    
    public void setTimeoutIdle(final Long timeoutIdle) {
        this.timeoutIdle = timeoutIdle;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void setStarted(final boolean started) {
        this.started = started;
    }
    
    public boolean isForceStarted() {
        return this.forceStarted;
    }
    
    public void setForceStarted(final boolean forceStarted) {
        this.forceStarted = forceStarted;
    }
    
    static {
        Tournament.tournaments = new ArrayList<Tournament>();
    }
}
