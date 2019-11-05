package io.dhoom.tournament;

import io.dhoom.*;
import io.dhoom.kit.*;
import java.util.*;
import org.bukkit.*;
import io.dhoom.arena.*;
import org.bukkit.plugin.*;

public class TournamentMatch
{
    private TournamentTeam firstTeam;
    private TournamentTeam secondTeam;
    private List<UUID> matchPlayers;
    private MatchState matchState;
    private int winndingId;
    
    public void start(final Practice plugin, final Kit defaultKit) {
        (this.matchPlayers = new ArrayList<UUID>()).addAll(this.firstTeam.getPlayers());
        this.matchPlayers.addAll(this.secondTeam.getPlayers());
        Bukkit.getScheduler().runTaskLater((Plugin)plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                final Arena arena = plugin.getManagerHandler().getArenaManager().getRandomArena();
                plugin.getManagerHandler().getDuelManager().createDuel(arena, defaultKit, false, TournamentMatch.this.firstTeam.getPlayers().get(0), TournamentMatch.this.secondTeam.getPlayers().get(0), TournamentMatch.this.firstTeam.getPlayers(), TournamentMatch.this.secondTeam.getPlayers(), true, false);
            }
        }, 40L);
        this.matchState = MatchState.FIGHTING;
        this.winndingId = 0;
    }
    
    public TournamentTeam getTournamentTeam(final TournamentTeam team) {
        if (this.firstTeam == team) {
            return this.firstTeam;
        }
        if (this.secondTeam == team) {
            return this.secondTeam;
        }
        return null;
    }
    
    public TournamentTeam getOtherTeam(final TournamentTeam tournamentTeam) {
        return this.getOtherDuelTeam(this.getTournamentTeam(tournamentTeam));
    }
    
    public TournamentTeam getOtherDuelTeam(final TournamentTeam duelTeam) {
        return (duelTeam == null) ? null : (duelTeam.equals(this.firstTeam) ? this.secondTeam : this.firstTeam);
    }
    
    public TournamentTeam getFirstTeam() {
        return this.firstTeam;
    }
    
    public void setFirstTeam(final TournamentTeam firstTeam) {
        this.firstTeam = firstTeam;
    }
    
    public TournamentTeam getSecondTeam() {
        return this.secondTeam;
    }
    
    public void setSecondTeam(final TournamentTeam secondTeam) {
        this.secondTeam = secondTeam;
    }
    
    public List<UUID> getMatchPlayers() {
        return this.matchPlayers;
    }
    
    public void setMatchPlayers(final List<UUID> matchPlayers) {
        this.matchPlayers = matchPlayers;
    }
    
    public MatchState getMatchState() {
        return this.matchState;
    }
    
    public void setMatchState(final MatchState matchState) {
        this.matchState = matchState;
    }
    
    public int getWinndingId() {
        return this.winndingId;
    }
    
    public void setWinndingId(final int winndingId) {
        this.winndingId = winndingId;
    }
    
    public enum MatchState
    {
        WAITING, 
        FIGHTING, 
        ENDING;
    }
}
