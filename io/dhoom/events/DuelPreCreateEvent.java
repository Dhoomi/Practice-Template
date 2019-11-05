package io.dhoom.events;

import org.bukkit.event.*;
import io.dhoom.kit.*;
import java.util.*;

public class DuelPreCreateEvent extends Event
{
    private static HandlerList handlerList;
    private Kit kit;
    private boolean ranked;
    private boolean premium;
    private UUID firstTeamPartyLeaderUUID;
    private UUID secondTeamPartyLeaderUUID;
    private List<UUID> firstTeam;
    private List<UUID> secondTeam;
    private UUID ffaPartyLeaderUUID;
    private List<UUID> ffaPlayers;
    
    public DuelPreCreateEvent(final Kit kit, final boolean ranked, final UUID firstTeamPartyLeaderUUID, final UUID secondTeamPartyLeaderUUID, final List<UUID> firstTeam, final List<UUID> secondTeam, final boolean premium) {
        this.kit = kit;
        this.ranked = ranked;
        this.premium = premium;
        this.firstTeamPartyLeaderUUID = firstTeamPartyLeaderUUID;
        this.secondTeamPartyLeaderUUID = secondTeamPartyLeaderUUID;
        this.firstTeam = new ArrayList<UUID>(firstTeam);
        this.secondTeam = new ArrayList<UUID>(secondTeam);
    }
    
    public DuelPreCreateEvent(final Kit kit, final UUID ffaPartyLeaderUUID, final List<UUID> ffaPlayers) {
        this.kit = kit;
        this.ffaPartyLeaderUUID = ffaPartyLeaderUUID;
        this.ffaPlayers = new ArrayList<UUID>(ffaPlayers);
    }
    
    public static HandlerList getHandlerList() {
        return DuelPreCreateEvent.handlerList;
    }
    
    public Kit getKit() {
        return this.kit;
    }
    
    public boolean isRanked() {
        return this.ranked;
    }
    
    public UUID getFirstTeamPartyLeaderUUID() {
        return this.firstTeamPartyLeaderUUID;
    }
    
    public UUID getSecondTeamPartyLeaderUUID() {
        return this.secondTeamPartyLeaderUUID;
    }
    
    public List<UUID> getFirstTeam() {
        return this.firstTeam;
    }
    
    public boolean isPremium() {
        return this.premium;
    }
    
    public List<UUID> getSecondTeam() {
        return this.secondTeam;
    }
    
    public UUID getFfaPartyLeaderUUID() {
        return this.ffaPartyLeaderUUID;
    }
    
    public List<UUID> getFfaPlayers() {
        return this.ffaPlayers;
    }
    
    public HandlerList getHandlers() {
        return DuelPreCreateEvent.handlerList;
    }
    
    static {
        DuelPreCreateEvent.handlerList = new HandlerList();
    }
}
