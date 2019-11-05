package io.dhoom.runnables.other;

import io.kipes.*;
import org.bukkit.entity.*;
import io.kipes.duel.*;
import io.kipes.player.*;

public class InsertMatchTask implements Runnable
{
    private Practice plugin;
    private Player player;
    private Duel duel;
    private int winningTeamId;
    private String elo_change;
    
    public InsertMatchTask(final Practice plugin, final Player player, final Duel duel, final int winningTeamId, final String elo_change) {
        this.player = player;
        this.plugin = plugin;
        this.winningTeamId = winningTeamId;
        this.elo_change = elo_change;
    }
    
    @Override
    public void run() {
        final PracticePlayer practicePlayer = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (practicePlayer != null) {}
    }
}
