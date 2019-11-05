package io.dhoom.player;

import java.util.*;

public class Match
{
    UUID matchId;
    boolean ranked;
    String items;
    String armor;
    String potions;
    int eloChangeWinner;
    int eloChangeLoser;
    int winningTeamId;
    private String firstTeam;
    private String secondTeam;
    
    public UUID getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final UUID matchId) {
        this.matchId = matchId;
    }
    
    public String getFirstTeam() {
        return this.firstTeam;
    }
    
    public void setFirstTeam(final String firstTeam) {
        this.firstTeam = firstTeam;
    }
    
    public String getSecondTeam() {
        return this.secondTeam;
    }
    
    public void setSecondTeam(final String secondTeam) {
        this.secondTeam = secondTeam;
    }
    
    public boolean isRanked() {
        return this.ranked;
    }
    
    public void setRanked(final boolean ranked) {
        this.ranked = ranked;
    }
    
    public String getItems() {
        return this.items;
    }
    
    public void setItems(final String items) {
        this.items = items;
    }
    
    public String getArmor() {
        return this.armor;
    }
    
    public void setArmor(final String armor) {
        this.armor = armor;
    }
    
    public String getPotions() {
        return this.potions;
    }
    
    public void setPotions(final String potions) {
        this.potions = potions;
    }
    
    public int getEloChangeWinner() {
        return this.eloChangeWinner;
    }
    
    public void setEloChangeWinner(final int eloChangeWinner) {
        this.eloChangeWinner = eloChangeWinner;
    }
    
    public int getEloChangeLoser() {
        return this.eloChangeLoser;
    }
    
    public void setEloChangeLoser(final int eloChangeLoser) {
        this.eloChangeLoser = eloChangeLoser;
    }
    
    public int getWinningTeamId() {
        return this.winningTeamId;
    }
    
    public void setWinningTeamId(final int winningTeamId) {
        this.winningTeamId = winningTeamId;
    }
}
