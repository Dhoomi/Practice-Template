package io.dhoom.scoreboard;

import io.kipes.*;
import io.kipes.scoreboard.provider.sidebar.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import io.kipes.util.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class ScoreboardHandler implements Listener
{
    private final Practice plugin;
    private final SidebarProvider defaultProvider;
    private Map<UUID, PlayerBoard> playerBoards;
    
    public ScoreboardHandler(final Practice plugin) {
        this.playerBoards = new HashMap<UUID, PlayerBoard>();
        this.plugin = plugin;
        this.defaultProvider = new LobbyScoreboardProvider(this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        for (final Player player : PlayerUtility.getOnlinePlayers()) {
            final PlayerBoard playerBoard = new PlayerBoard(plugin, player);
            this.setPlayerBoard(player.getUniqueId(), playerBoard);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        for (PlayerBoard playerBoard : this.playerBoards.values()) {}
        final PlayerBoard board2 = new PlayerBoard(this.plugin, player);
        this.setPlayerBoard(uuid, board2);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }
    
    public PlayerBoard getPlayerBoard(final UUID uuid) {
        return this.playerBoards.get(uuid);
    }
    
    public void setPlayerBoard(final UUID uuid, final PlayerBoard board) {
        this.playerBoards.put(uuid, board);
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.defaultProvider, 2L);
    }
    
    public void clearBoards() {
        final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}
