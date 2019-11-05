package io.dhoom;

import org.bukkit.plugin.java.*;
import java.util.regex.*;
import io.dhoom.util.database.*;
import io.dhoom.scoreboard.provider.*;
import io.dhoom.manager.*;
import io.dhoom.scoreboard.config.*;
import io.dhoom.lang.*;
import org.bukkit.configuration.file.*;
import io.dhoom.util.*;
import org.bukkit.*;
import io.dhoom.scoreboard.*;
import io.dhoom.scoreboard.provider.prefix.*;
import org.bukkit.event.*;
import io.dhoom.listeners.*;
import io.dhoom.tournament.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import io.dhoom.settings.*;
import io.dhoom.commands.*;

public class Practice extends JavaPlugin
{
    public static final Pattern UUID_PATTER;
    public static Pattern splitPattern;
    private static Practice instance;
    PracticeDatabase practiceDatabase;
    private PrefixProvider prefixProvider;
    private ManagerHandler managerHandler;
    private Location spawn;
    private ScoreboardConfig scoreboardConfig;
    
    public static Practice getInstance() {
        return Practice.instance;
    }
    
    public void onEnable() {
        Practice.instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        final ConfigurationWrapper languageConfig = new ConfigurationWrapper("lang.yml", this);
        languageConfig.saveDefault();
        Lang.getLang().add((FileConfiguration)languageConfig.getConfig());
        final ConfigurationWrapper scoreboardConfig = new ConfigurationWrapper("scoreboard.yml", this);
        scoreboardConfig.saveDefault();
        if (this.getConfig().contains("spawn")) {
            this.spawn = LocationSerializer.deserializeLocation(this.getConfig().getString("spawn"));
        }
        this.scoreboardConfig = new ScoreboardConfig((FileConfiguration)scoreboardConfig.getConfig());
        SidebarProvider.SCOREBOARD_TITLE = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("SCOREBOARD_TITLE"));
        this.practiceDatabase = new PracticeDatabase(this);
        this.managerHandler = new ManagerHandler(this);
        this.registerListeners();
        this.registerCommands();
        if (this.getServer().getPluginManager().getPlugin("Groups") != null) {
            this.prefixProvider = new GroupProvider();
            this.getLogger().info("Using GroupProvider as PrefixProvider");
        }
        else {
            this.prefixProvider = new NoOpPrefixProvider();
            System.err.print("Permission Plugin Not Found. Install Vault and Permission Plugin for full Support.");
        }
    }
    
    public void onDisable() {
        this.reloadConfig();
        this.managerHandler.disable();
    }
    
    private void registerListeners() {
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents((Listener)new PlayerListener(this), (Plugin)this);
        pm.registerEvents((Listener)new PearlListener(this), (Plugin)this);
        pm.registerEvents((Listener)new HitDetectionListener(this), (Plugin)this);
        pm.registerEvents((Listener)new InventoryListener(this), (Plugin)this);
        pm.registerEvents((Listener)new EntityListener(this), (Plugin)this);
        pm.registerEvents((Listener)new DuelListener(this), (Plugin)this);
        pm.registerEvents((Listener)new BlockListener(this), (Plugin)this);
        pm.registerEvents((Listener)new TournamentListener(), (Plugin)this);
    }
    
    private void registerCommands() {
        this.getCommand("arena").setExecutor((CommandExecutor)new ArenaCommand(this));
        this.getCommand("duel").setExecutor((CommandExecutor)new DuelCommand(this));
        this.getCommand("accept").setExecutor((CommandExecutor)new AcceptCommand(this));
        this.getCommand("kit").setExecutor((CommandExecutor)new KitCommand(this));
        this.getCommand("spectate").setExecutor((CommandExecutor)new SpectateCommand(this));
        this.getCommand("build").setExecutor((CommandExecutor)new AdminCommand(this));
        this.getCommand("inventory").setExecutor((CommandExecutor)new InventoryCommand(this));
        this.getCommand("party").setExecutor((CommandExecutor)new PartyCommand(this));
        this.getCommand("reset").setExecutor((CommandExecutor)new ResetEloCommand(this));
        this.getCommand("credits").setExecutor((CommandExecutor)new CreditsCommand(this));
        this.getCommand("setspawn").setExecutor((CommandExecutor)new SetSpawnCommand(this));
        this.getCommand("ping").setExecutor((CommandExecutor)new PingCommand(this));
        this.getCommand("setting").setExecutor((CommandExecutor)new SettingsHandler(this));
        this.getCommand("tokens").setExecutor((CommandExecutor)new PremiumTokensCommand(this));
        this.getCommand("host").setExecutor((CommandExecutor)new HostCommand(this));
        this.getCommand("tournament").setExecutor((CommandExecutor)new TournamentCommand(this));
        this.getCommand("join").setExecutor((CommandExecutor)new JoinCommand(this));
        this.getCommand("leave").setExecutor((CommandExecutor)new LeaveCommand(this));
        this.getCommand("matches").setExecutor((CommandExecutor)new MatchesCommand(this));
    }
    
    public ManagerHandler getManagerHandler() {
        return this.managerHandler;
    }
    
    public PracticeDatabase getPracticeDatabase() {
        return this.practiceDatabase;
    }
    
    public Location getSpawn() {
        return this.spawn;
    }
    
    public void setSpawn(final Location spawn) {
        this.spawn = spawn;
    }
    
    public PrefixProvider getPrefixProvider() {
        return this.prefixProvider;
    }
    
    public ScoreboardConfig getScoreboardConfig() {
        return this.scoreboardConfig;
    }
    
    static {
        Practice.splitPattern = Pattern.compile("\\s");
        UUID_PATTER = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    }
}
