package io.dhoom.schedule;

import org.bukkit.scheduler.*;
import io.dhoom.*;
import org.bukkit.plugin.*;

public class ScheduleTimer extends BukkitRunnable
{
    private Practice plugin;
    
    public ScheduleTimer(final Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)plugin, (BukkitRunnable)this, 20L, 20L);
    }
    
    public void run() {
        ScheduleHandler.runSchedule();
    }
}
