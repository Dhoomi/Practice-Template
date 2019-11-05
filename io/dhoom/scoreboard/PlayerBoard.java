package io.dhoom.scoreboard;

import org.bukkit.entity.*;
import io.kipes.*;
import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.*;
import org.bukkit.plugin.*;
import java.util.*;

public class PlayerBoard
{
    public final BufferedObjective bufferedObjective;
    private final Scoreboard scoreboard;
    private final Player player;
    private final Practice plugin;
    private boolean sidebarVisible;
    private boolean removed;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;
    
    public PlayerBoard(final Practice plugin, final Player player) {
        this.sidebarVisible = false;
        this.removed = false;
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        player.setScoreboard(this.scoreboard);
    }
    
    public void remove() {
        this.removed = true;
        if (this.scoreboard != null) {
            final Scoreboard scoreboard = this.scoreboard;
            synchronized (scoreboard) {
                for (final Team team : this.scoreboard.getTeams()) {
                    team.unregister();
                }
                for (final Objective objective : this.scoreboard.getObjectives()) {
                    objective.unregister();
                }
            }
        }
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void setSidebarVisible(final boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }
    
    public void setDefaultSidebar(final SidebarProvider provider, final long updateInterval) {
        if (provider != null && provider.equals(this.defaultProvider)) {
            return;
        }
        this.defaultProvider = provider;
        if (this.runnable != null) {
            this.runnable.cancel();
        }
        if (provider == null) {
            this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        (this.runnable = new BukkitRunnable() {
            public void run() {
                if (PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if (provider.equals(PlayerBoard.this.defaultProvider)) {
                    PlayerBoard.this.updateObjective();
                }
            }
        }).runTaskTimer((Plugin)this.plugin, updateInterval, updateInterval);
    }
    
    private void updateObjective() {
        final SidebarProvider sidebarProvider;
        final SidebarProvider provider = sidebarProvider = ((this.temporaryProvider != null) ? this.temporaryProvider : this.defaultProvider);
        if (provider == null) {
            this.bufferedObjective.setVisible(false);
        }
        else {
            final List<SidebarEntry> lines = provider.getLines(this.player);
            if (lines == null) {
                this.bufferedObjective.setVisible(false);
                return;
            }
            this.bufferedObjective.setVisible(true);
            this.bufferedObjective.setTitle(provider.getTitle(this.player));
            this.bufferedObjective.setAllLines(lines);
            this.bufferedObjective.flip();
        }
    }
}
