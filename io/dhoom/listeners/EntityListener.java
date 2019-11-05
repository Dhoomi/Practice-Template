package io.dhoom.listeners;

import io.dhoom.*;
import org.bukkit.entity.*;
import io.dhoom.player.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;

public class EntityListener implements Listener
{
    private Practice plugin;
    
    public EntityListener(final Practice plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player damagedPlayer = (Player)e.getEntity();
            final Player attackerPlayer = (Player)e.getDamager();
            final PracticePlayer damagedPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(damagedPlayer);
            final PracticePlayer attackerPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(attackerPlayer);
            if (damagedPracPlayer.getCurrentState() != PlayerState.FIGHTING || attackerPracPlayer.getCurrentState() != PlayerState.FIGHTING) {
                e.setCancelled(true);
            }
            if (damagedPracPlayer.getTeamNumber() == 0) {
                return;
            }
            final int damagedTeamNumber = damagedPracPlayer.getTeamNumber();
            final int attackerTeamNumber;
            if (damagedTeamNumber == (attackerTeamNumber = attackerPracPlayer.getTeamNumber())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        final PracticePlayer practicePlayer;
        if (e.getEntity() instanceof Player && (practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer((Player)e.getEntity())).getCurrentState() != PlayerState.FIGHTING) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (this.plugin.getSpawn() == null) {
                    e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation());
                }
                else {
                    e.getEntity().teleport(this.plugin.getSpawn());
                }
            }
            e.setCancelled(true);
        }
    }
}
