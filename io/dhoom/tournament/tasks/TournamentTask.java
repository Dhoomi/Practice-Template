package io.kipes.tournament.tasks;

import org.bukkit.scheduler.*;
import io.dhoom.tournament.*;
import io.dhoom.runnables.other.*;
import org.bukkit.plugin.*;

public class TournamentTask extends BukkitRunnable
{
    private Tournament tournament;
    
    public TournamentTask(final Tournament tournament) {
        this.tournament = tournament;
    }
    
    public void run() {
        this.tournament.generateRoundMatches();
        this.tournament.getPlugin().getServer().getScheduler().runTaskAsynchronously((Plugin)this.tournament.getPlugin(), (Runnable)new UpdateInventoryTask(this.tournament.getPlugin(), UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
    }
}
