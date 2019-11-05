package io.dhoom.manager;

import io.dhoom.*;
import io.dhoom.util.*;
import io.dhoom.manager.managers.*;
import io.dhoom.scoreboard.*;
import org.bukkit.plugin.java.*;
import io.dhoom.schedule.*;

public class ManagerHandler
{
    private Practice plugin;
    private ConfigFile scheduleConfig;
    private ArenaManager arenaManager;
    private DuelManager duelManager;
    private KitManager kitManager;
    private EditorManager editorManager;
    private ItemManager itemManager;
    private PracticePlayerManager practicePlayerManager;
    private InventoryManager inventoryManager;
    private QueueManager queueManager;
    private RequestManager requestManager;
    private SpectatorManager spectatorManager;
    private InventorySnapshotManager inventorySnapshotManager;
    private PartyManager partyManager;
    private ScoreboardHandler scoreboardHandler;
    
    public ManagerHandler(final Practice plugin) {
        this.plugin = plugin;
        this.loadManagers();
    }
    
    private void loadManagers() {
        this.scheduleConfig = new ConfigFile(this.plugin, "schedule");
        this.arenaManager = new ArenaManager(this);
        this.duelManager = new DuelManager(this.plugin, this);
        this.kitManager = new KitManager(this);
        this.editorManager = new EditorManager(this);
        this.itemManager = new ItemManager(this);
        this.practicePlayerManager = new PracticePlayerManager(this);
        this.queueManager = new QueueManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.requestManager = new RequestManager(this);
        this.spectatorManager = new SpectatorManager(this);
        this.inventorySnapshotManager = new InventorySnapshotManager(this);
        this.partyManager = new PartyManager(this);
        this.scoreboardHandler = new ScoreboardHandler(this.plugin);
        ScheduleHandler.setSchedules(this.scheduleConfig);
        new ScheduleTimer(this.plugin);
    }
    
    public void disable() {
        this.arenaManager.disable();
        this.kitManager.disable();
        this.practicePlayerManager.disable();
        this.scoreboardHandler.clearBoards();
    }
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }
    
    public DuelManager getDuelManager() {
        return this.duelManager;
    }
    
    public KitManager getKitManager() {
        return this.kitManager;
    }
    
    public EditorManager getEditorManager() {
        return this.editorManager;
    }
    
    public ItemManager getItemManager() {
        return this.itemManager;
    }
    
    public PracticePlayerManager getPracticePlayerManager() {
        return this.practicePlayerManager;
    }
    
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
    
    public QueueManager getQueueManager() {
        return this.queueManager;
    }
    
    public RequestManager getRequestManager() {
        return this.requestManager;
    }
    
    public SpectatorManager getSpectatorManager() {
        return this.spectatorManager;
    }
    
    public InventorySnapshotManager getInventorySnapshotManager() {
        return this.inventorySnapshotManager;
    }
    
    public PartyManager getPartyManager() {
        return this.partyManager;
    }
    
    public ScoreboardHandler getScoreboardHandler() {
        return this.scoreboardHandler;
    }
}
