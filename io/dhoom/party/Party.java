package io.dhoom.party;

import org.bukkit.*;
import java.util.*;
import org.bukkit.entity.*;

public class Party
{
    private List<UUID> memberUUIDs;
    private UUID partyLeader;
    private String leaderName;
    private PartyState partyState;
    private boolean open;
    
    public Party(final UUID partyLeader, final String leaderName) {
        this.memberUUIDs = new ArrayList<UUID>();
        this.partyLeader = partyLeader;
        this.leaderName = leaderName;
        this.partyState = PartyState.LOBBY;
    }
    
    public void addMember(final UUID uuid) {
        this.memberUUIDs.add(uuid);
    }
    
    public void removeMember(final UUID uuid) {
        this.memberUUIDs.remove(uuid);
    }
    
    public List<UUID> getMembers() {
        return this.memberUUIDs;
    }
    
    public UUID getLeader() {
        return this.partyLeader;
    }
    
    public String getLeaderName() {
        return this.leaderName;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public PartyState getPartyState() {
        return this.partyState;
    }
    
    public void setPartyState(final PartyState state) {
        this.partyState = state;
    }
    
    public int getSize() {
        return this.getMembers().size() + 1;
    }
    
    public List<UUID> getAllMembersOnline() {
        final ArrayList<UUID> membersOnline = new ArrayList<UUID>();
        for (final UUID memberUUID : this.memberUUIDs) {
            final Player member = Bukkit.getPlayer(memberUUID);
            if (member == null) {
                continue;
            }
            membersOnline.add(member.getUniqueId());
        }
        membersOnline.add(this.partyLeader);
        return membersOnline;
    }
}
