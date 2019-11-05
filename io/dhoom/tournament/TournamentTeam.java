package io.dhoom.tournament;

import org.bukkit.*;
import java.util.*;
import org.bukkit.entity.*;

public class TournamentTeam
{
    private List<UUID> players;
    
    public TournamentTeam() {
        this.players = new ArrayList<UUID>();
    }
    
    public void sendMessage(final String message) {
        for (final UUID uuid : this.players) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.sendMessage(message);
        }
    }
    
    public List<UUID> getPlayers() {
        return this.players;
    }
    
    public void setPlayers(final List<UUID> players) {
        this.players = players;
    }
}
