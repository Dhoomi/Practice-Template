package io.dhoom.manager.managers;

import io.dhoom.manager.*;
import java.util.concurrent.*;
import org.bukkit.*;
import io.dhoom.runnables.other.*;
import io.dhoom.runnables.other.*;
import org.bukkit.plugin.*;
import java.util.*;

public class QueueManager extends Manager
{
    private Map<String, UUID> unrankedKitQueueMap;
    private Map<String, List<UUID>> rankedKitQueueMap;
    private Map<String, UUID> partyUnrankedKitQueueMap;
    private Map<String, List<UUID>> partyRankedKitQueueMap;
    private Map<String, List<UUID>> premiumKitQueueMap;
    private int rankedQueueCount;
    private int unrankedQueueCount;
    private int premiumQueueCount;
    
    public QueueManager(final ManagerHandler handler) {
        super(handler);
        this.unrankedKitQueueMap = new ConcurrentHashMap<String, UUID>();
        this.rankedKitQueueMap = new ConcurrentHashMap<String, List<UUID>>();
        this.partyUnrankedKitQueueMap = new ConcurrentHashMap<String, UUID>();
        this.partyRankedKitQueueMap = new ConcurrentHashMap<String, List<UUID>>();
        this.premiumKitQueueMap = new ConcurrentHashMap<String, List<UUID>>();
        this.rankedQueueCount = 0;
        this.unrankedQueueCount = 0;
        this.premiumQueueCount = 0;
    }
    
    public boolean isUnrankedQueueEmpty(final String kitName) {
        return !this.unrankedKitQueueMap.containsKey(kitName);
    }
    
    public void addToUnrankedQueue(final String kitName, final UUID uuid) {
        this.unrankedKitQueueMap.put(kitName, uuid);
        ++this.unrankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_SOLO));
    }
    
    public UUID getQueuedForUnrankedQueue(final String kitName) {
        return this.unrankedKitQueueMap.get(kitName);
    }
    
    public void removePlayerFromUnrankedQueue(final String kitName) {
        this.unrankedKitQueueMap.remove(kitName);
        --this.unrankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_SOLO));
    }
    
    public boolean isRankedQueueEmpty(final String kitName) {
        return !this.rankedKitQueueMap.containsKey(kitName) || this.rankedKitQueueMap.get(kitName).size() == 0;
    }
    
    public void addToRankedQueue(final String kitName, final UUID uuid) {
        if (!this.rankedKitQueueMap.containsKey(kitName)) {
            this.rankedKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.rankedKitQueueMap.get(kitName);
        uuidList.add(uuid);
        this.rankedKitQueueMap.put(kitName, uuidList);
        ++this.rankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.RANKED_SOLO));
    }
    
    public List<UUID> getQueuedForRankedQueue(final String kitName) {
        if (!this.rankedKitQueueMap.containsKey(kitName)) {
            return null;
        }
        return this.rankedKitQueueMap.get(kitName);
    }
    
    public void removePlayerFromRankedQueue(final String kitName, final UUID playerUuid) {
        if (!this.rankedKitQueueMap.containsKey(kitName)) {
            this.rankedKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.rankedKitQueueMap.get(kitName);
        uuidList.remove(playerUuid);
        this.rankedKitQueueMap.put(kitName, uuidList);
        --this.rankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.RANKED_SOLO));
    }
    
    public boolean isPremiumQueueEmpty(final String kitName) {
        return !this.premiumKitQueueMap.containsKey(kitName) || this.premiumKitQueueMap.get(kitName).size() == 0;
    }
    
    public void addToPremiumQueue(final String kitName, final UUID uuid) {
        if (!this.premiumKitQueueMap.containsKey(kitName)) {
            this.premiumKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.premiumKitQueueMap.get(kitName);
        uuidList.add(uuid);
        this.premiumKitQueueMap.put(kitName, uuidList);
        ++this.premiumQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.PREMIUM_SOLO));
    }
    
    public List<UUID> getQueuedForPremiumQueue(final String kitName) {
        if (!this.premiumKitQueueMap.containsKey(kitName)) {
            return null;
        }
        return this.premiumKitQueueMap.get(kitName);
    }
    
    public void removePlayerFromPremiumQueue(final String kitName, final UUID playerUuid) {
        if (!this.premiumKitQueueMap.containsKey(kitName)) {
            this.premiumKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.premiumKitQueueMap.get(kitName);
        uuidList.remove(playerUuid);
        this.premiumKitQueueMap.put(kitName, uuidList);
        --this.premiumQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.PREMIUM_SOLO));
    }
    
    public void unqueueSingleQueue(final UUID playerUuid) {
        for (final Map.Entry<String, UUID> mapEntry : this.unrankedKitQueueMap.entrySet()) {
            final String kitName = mapEntry.getKey();
            final UUID queued = mapEntry.getValue();
            if (queued != playerUuid) {
                continue;
            }
            --this.unrankedQueueCount;
            this.unrankedKitQueueMap.remove(kitName);
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_SOLO));
        }
        for (final Map.Entry mapEntry2 : this.rankedKitQueueMap.entrySet()) {
            final String kitName = mapEntry2.getKey();
            final List queuedUuidList = mapEntry2.getValue();
            if (!queuedUuidList.contains(playerUuid)) {
                continue;
            }
            queuedUuidList.remove(playerUuid);
            this.rankedKitQueueMap.put(kitName, queuedUuidList);
            --this.rankedQueueCount;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.RANKED_SOLO));
        }
        for (final Map.Entry mapEntry2 : this.premiumKitQueueMap.entrySet()) {
            final String kitName = mapEntry2.getKey();
            final List queuedUuidList = mapEntry2.getValue();
            if (!queuedUuidList.contains(playerUuid)) {
                continue;
            }
            queuedUuidList.remove(playerUuid);
            this.premiumKitQueueMap.put(kitName, queuedUuidList);
            --this.premiumQueueCount;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.PREMIUM_SOLO));
        }
    }
    
    public boolean isUnrankedPartyQueueEmpty(final String kitName) {
        return !this.partyUnrankedKitQueueMap.containsKey(kitName);
    }
    
    public void addToPartyUnrankedQueue(final String kitName, final UUID uuid) {
        this.partyUnrankedKitQueueMap.put(kitName, uuid);
        ++this.unrankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
    }
    
    public UUID getQueuedForPartyUnrankedQueue(final String kitName) {
        return this.partyUnrankedKitQueueMap.get(kitName);
    }
    
    public void removePartyFromPartyUnrankedQueue(final String kitName) {
        this.partyUnrankedKitQueueMap.remove(kitName);
        --this.unrankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
    }
    
    public boolean isRankedPartyQueueEmpty(final String kitName) {
        return !this.partyRankedKitQueueMap.containsKey(kitName) || this.partyRankedKitQueueMap.get(kitName).size() == 0;
    }
    
    public void addToPartyRankedQueue(final String kitName, final UUID uuid) {
        if (!this.partyRankedKitQueueMap.containsKey(kitName)) {
            this.partyRankedKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.partyRankedKitQueueMap.get(kitName);
        uuidList.add(uuid);
        this.partyRankedKitQueueMap.put(kitName, uuidList);
        ++this.rankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.RANKED_PARTY));
    }
    
    public List<UUID> getQueuedForPartyRankedQueue(final String kitName) {
        if (!this.partyRankedKitQueueMap.containsKey(kitName)) {
            return null;
        }
        return this.partyRankedKitQueueMap.get(kitName);
    }
    
    public void removePlayerFromPartyRankedQueue(final String kitName, final UUID playerUuid) {
        if (!this.partyRankedKitQueueMap.containsKey(kitName)) {
            this.partyRankedKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.partyRankedKitQueueMap.get(kitName);
        uuidList.remove(playerUuid);
        this.partyRankedKitQueueMap.put(kitName, uuidList);
        --this.rankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.RANKED_PARTY));
    }
    
    public void unqueuePartyQueue(final UUID leaderUuid) {
        for (final Map.Entry<String, UUID> mapEntry : this.partyUnrankedKitQueueMap.entrySet()) {
            final String kitName = mapEntry.getKey();
            final UUID queued = mapEntry.getValue();
            if (queued != leaderUuid) {
                continue;
            }
            this.partyUnrankedKitQueueMap.remove(kitName);
            --this.unrankedQueueCount;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
        }
        for (final Map.Entry mapEntry2 : this.partyRankedKitQueueMap.entrySet()) {
            final String kitName = mapEntry2.getKey();
            final List queuedUuidList = mapEntry2.getValue();
            if (!queuedUuidList.contains(leaderUuid)) {
                continue;
            }
            queuedUuidList.remove(leaderUuid);
            this.partyRankedKitQueueMap.put(kitName, queuedUuidList);
            --this.rankedQueueCount;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.RANKED_PARTY));
        }
    }
    
    public Map<String, List<UUID>> getRankedKitQueueMap() {
        return this.rankedKitQueueMap;
    }
    
    public Map<String, List<UUID>> getPartyRankedKitQueueMap() {
        return this.partyRankedKitQueueMap;
    }
    
    public Map<String, List<UUID>> getPremiumKitQueueMap() {
        return this.premiumKitQueueMap;
    }
    
    public int getTotalInQueues() {
        return this.rankedQueueCount + this.unrankedQueueCount + this.premiumQueueCount;
    }
}
