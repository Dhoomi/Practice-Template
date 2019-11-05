package io.dhoom.scoreboard.provider.sidebar;

import io.kipes.*;
import org.bukkit.entity.*;
import java.util.*;
import io.kipes.scoreboard.*;
import io.kipes.scoreboard.config.*;
import io.kipes.player.*;

public class LobbyScoreboardProvider extends SidebarProvider
{
    private final kPractice plugin;
    
    public LobbyScoreboardProvider(final kPractice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player player) {
        return LobbyScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer == null || !practicePlayer.getSettings().isScoreboard()) {
            return null;
        }
        final List<String> entries = this.plugin.getScoreboardConfig().get(ScoreboardType.LOBBY);
        return this.preprocess(player, entries);
    }
}
