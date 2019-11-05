package io.dhoom.scoreboard.provider.sidebar;

import io.kipes.*;
import org.bukkit.entity.*;
import java.util.*;
import io.kipes.scoreboard.*;
import io.kipes.scoreboard.config.*;
import io.kipes.player.*;
import io.kipes.duel.*;

public class DuelScoreboardProvider extends SidebarProvider
{
    private final kPractice plugin;
    
    public DuelScoreboardProvider(final kPractice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player paramPlayer) {
        return DuelScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer == null || !practicePlayer.getSettings().isScoreboard()) {
            return null;
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId());
        final List<String> entries = (duel == null) ? this.plugin.getScoreboardConfig().get(ScoreboardType.LOADING) : ((duel.getFfaPlayers() != null && duel.getFfaPlayers().size() > 0) ? this.plugin.getScoreboardConfig().get(ScoreboardType.DUEL_FFA) : this.plugin.getScoreboardConfig().get(ScoreboardType.DUEL_NORMAL));
        return this.preprocess(player, entries);
    }
}
