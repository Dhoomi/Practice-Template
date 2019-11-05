package io.dhoom.duel;

import io.dhoom.player.*;
import java.util.*;
import org.bukkit.entity.*;
import com.google.common.base.*;

public class Duel
{
    private String arenaName;
    private String kitName;
    private UUID uuid;
    private boolean ranked;
    private boolean premium;
    private DuelState duelState;
    private UUID firstTeamPartyLeaderUUID;
    private UUID secondTeamPartyLeaderUUID;
    private List<UUID> firstTeam;
    private List<UUID> secondTeam;
    private List<UUID> firstTeamAlive;
    private List<UUID> secondTeamAlive;
    private UUID ffaPartyLeaderUUID;
    private List<UUID> ffaPlayers;
    private List<UUID> ffaPlayersAlive;
    private List<UUID> spectators;
    private Map<UUID, PlayerInventorySnapshot> playerUUIDToSnapshot;
    private Map<UUID, UUID> playerUUIDtoSnapshotUUID;
    private long startMatchTime;
    private long endMatchTime;
    private boolean tournament;
    
    public Duel(final String arenaName, final String kitName, final UUID uuid, final boolean ranked, final UUID firstTeamPartyLeaderUUID, final UUID secondTeamPartyLeaderUUID, final List<UUID> firstTeam, final List<UUID> secondTeam, final boolean torunament, final boolean premium) {
        this.spectators = new ArrayList<UUID>();
        this.playerUUIDToSnapshot = new HashMap<UUID, PlayerInventorySnapshot>();
        this.playerUUIDtoSnapshotUUID = new HashMap<UUID, UUID>();
        this.arenaName = arenaName;
        this.kitName = kitName;
        this.uuid = uuid;
        this.ranked = ranked;
        this.firstTeamPartyLeaderUUID = firstTeamPartyLeaderUUID;
        this.secondTeamPartyLeaderUUID = secondTeamPartyLeaderUUID;
        this.firstTeam = new ArrayList<UUID>(firstTeam);
        this.secondTeam = new ArrayList<UUID>(secondTeam);
        this.firstTeamAlive = new ArrayList<UUID>(firstTeam);
        this.secondTeamAlive = new ArrayList<UUID>(secondTeam);
        this.duelState = DuelState.STARTING;
        this.premium = premium;
        this.tournament = torunament;
    }
    
    public Duel(final String arenaName, final String kitName, final UUID uuid, final UUID ffaPartyLeaderUUID, final List<UUID> ffaPlayers) {
        this.spectators = new ArrayList<UUID>();
        this.playerUUIDToSnapshot = new HashMap<UUID, PlayerInventorySnapshot>();
        this.playerUUIDtoSnapshotUUID = new HashMap<UUID, UUID>();
        this.arenaName = arenaName;
        this.kitName = kitName;
        this.uuid = uuid;
        this.ffaPartyLeaderUUID = ffaPartyLeaderUUID;
        this.ffaPlayers = new ArrayList<UUID>(ffaPlayers);
        this.ffaPlayersAlive = new ArrayList<UUID>(ffaPlayers);
        this.duelState = DuelState.STARTING;
    }
    
    public void addSnapshot(final UUID uuid, final PlayerInventorySnapshot playerInventorySnapshot) {
        this.playerUUIDToSnapshot.put(uuid, playerInventorySnapshot);
    }
    
    public void addUUIDSnapshot(final UUID playerUUID, final UUID snapshotUUID) {
        this.playerUUIDtoSnapshotUUID.put(playerUUID, snapshotUUID);
    }
    
    public void addSpectator(final UUID uuid) {
        this.spectators.add(uuid);
    }
    
    public void removeSpectator(final UUID uuid) {
        this.spectators.remove(uuid);
    }
    
    public void killPlayerFFA(final UUID uuid) {
        this.ffaPlayersAlive.remove(uuid);
    }
    
    public void killPlayerFirstTeam(final UUID uuid) {
        this.firstTeamAlive.remove(uuid);
    }
    
    public void killPlayerSecondTeam(final UUID uuid) {
        this.secondTeamAlive.remove(uuid);
    }
    
    public String getArenaName() {
        return this.arenaName;
    }
    
    public String getKitName() {
        return this.kitName;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public boolean isRanked() {
        return this.ranked;
    }
    
    public DuelState getDuelState() {
        return this.duelState;
    }
    
    public void setDuelState(final DuelState duelState) {
        this.duelState = duelState;
    }
    
    public UUID getFirstTeamPartyLeaderUUID() {
        return this.firstTeamPartyLeaderUUID;
    }
    
    public UUID getSecondTeamPartyLeaderUUID() {
        return this.secondTeamPartyLeaderUUID;
    }
    
    public UUID getFfaPartyLeaderUUID() {
        return this.ffaPartyLeaderUUID;
    }
    
    public List<UUID> getFfaPlayers() {
        return this.ffaPlayers;
    }
    
    public List<UUID> getFfaPlayersAlive() {
        return this.ffaPlayersAlive;
    }
    
    public List<UUID> getFirstTeam() {
        return this.firstTeam;
    }
    
    public List<UUID> getSecondTeam() {
        return this.secondTeam;
    }
    
    public List<UUID> getFirstTeamAlive() {
        return this.firstTeamAlive;
    }
    
    public List<UUID> getSecondTeamAlive() {
        return this.secondTeamAlive;
    }
    
    public List<UUID> getSpectators() {
        return this.spectators;
    }
    
    public boolean isPremium() {
        return this.premium;
    }
    
    public Map<UUID, PlayerInventorySnapshot> getPlayerUUIDToSnapshotMap() {
        return this.playerUUIDToSnapshot;
    }
    
    public Map<UUID, UUID> getPlayerUUIDtoSnapshotUUIDMap() {
        return this.playerUUIDtoSnapshotUUID;
    }
    
    public long getStartDuration() {
        return System.currentTimeMillis() - this.startMatchTime;
    }
    
    public long getEndMatchTime() {
        return this.endMatchTime;
    }
    
    public void setEndMatchTime(final long endMatchTime) {
        this.endMatchTime = endMatchTime;
    }
    
    public long getFinalDuration() {
        return this.endMatchTime - this.startMatchTime;
    }
    
    public void setStartMatchTime(final long startMatchTime) {
        this.startMatchTime = startMatchTime;
    }
    
    public boolean isTournament() {
        return this.tournament;
    }
    
    public UUID getOtherTeamLeader(final Player player) {
        Preconditions.checkNotNull((Object)player, (Object)"Player cannot be null");
        if (this.firstTeam.contains(player.getUniqueId())) {
            return this.secondTeamPartyLeaderUUID;
        }
        if (this.secondTeam.contains(player.getUniqueId())) {
            return this.firstTeamPartyLeaderUUID;
        }
        throw new IllegalArgumentException("Player " + player.getName() + " is not in duel.");
    }
    
    public List<UUID> getDuelTeam(final Player player) {
        Preconditions.checkNotNull((Object)player, (Object)"Player cannot be null");
        if (this.firstTeam.contains(player.getUniqueId())) {
            return this.firstTeam;
        }
        if (this.secondTeam.contains(player.getUniqueId())) {
            return this.secondTeam;
        }
        throw new IllegalArgumentException("Player " + player.getName() + " is not in duel.");
    }
    
    public List<UUID> getOtherDuelTeam(final Player player) {
        return this.getOtherDuelTeam(this.getDuelTeam(player));
    }
    
    public List<UUID> getOtherDuelTeam(final List<UUID> duelTeam) {
        return (duelTeam == null) ? null : (duelTeam.equals(this.firstTeam) ? this.secondTeam : this.firstTeam);
    }
}
