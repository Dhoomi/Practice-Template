package io.dhoom.manager.managers;

import io.dhoom.duel.*;
import io.dhoom.manager.*;
import java.util.*;
import org.bukkit.entity.*;
import java.util.concurrent.*;
import io.dhoom.util.*;

public class RequestManager extends Manager
{
    private Map<UUID, Map<UUID, DuelRequest>> duelRequestMap;
    private Map<UUID, List<UUID>> partyRequestMap;
    
    public RequestManager(final ManagerHandler handler) {
        super(handler);
        this.duelRequestMap = new HashMap<UUID, Map<UUID, DuelRequest>>();
        this.partyRequestMap = new HashMap<UUID, List<UUID>>();
    }
    
    public void addDuelRequest(final Player requested, final Player requester, final DuelRequest request) {
        if (!this.hasDuelRequests(requested)) {
            this.duelRequestMap.put(requested.getUniqueId(), new TtlHashMap<UUID, DuelRequest>(TimeUnit.SECONDS, 15L));
        }
        this.duelRequestMap.get(requested.getUniqueId()).put(requester.getUniqueId(), request);
    }
    
    public boolean hasDuelRequestFromPlayer(final Player requested, final Player requester) {
        return this.duelRequestMap.get(requested.getUniqueId()).containsKey(requester.getUniqueId());
    }
    
    public boolean hasDuelRequests(final Player player) {
        return this.duelRequestMap.containsKey(player.getUniqueId());
    }
    
    public void removeDuelRequest(final Player player, final Player requester) {
        this.duelRequestMap.get(player.getUniqueId()).remove(requester.getUniqueId());
    }
    
    public DuelRequest getDuelRequest(final Player requested, final Player requester) {
        return this.duelRequestMap.get(requested.getUniqueId()).get(requester.getUniqueId());
    }
    
    public void addPartyRequest(final Player requested, final Player requester) {
        if (!this.hasPartyRequests(requested)) {
            this.partyRequestMap.put(requested.getUniqueId(), new TtlArrayList<UUID>(TimeUnit.SECONDS, 15L));
        }
        this.partyRequestMap.get(requested.getUniqueId()).add(requester.getUniqueId());
    }
    
    public boolean hasPartyRequestFromPlayer(final Player requested, final Player requester) {
        return this.partyRequestMap.get(requested.getUniqueId()).contains(requester.getUniqueId());
    }
    
    public boolean hasPartyRequests(final Player player) {
        return this.partyRequestMap.containsKey(player.getUniqueId());
    }
    
    public void removePartyRequest(final Player requested, final Player requester) {
        this.partyRequestMap.get(requested.getUniqueId()).remove(requester.getUniqueId());
    }
}
