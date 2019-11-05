package io.dhoom.util;

import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.potion.*;
import com.google.common.base.*;

public class UtilPlayer
{
    public static void clear(final Player player) {
        for (final PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setMaximumNoDamageTicks(20);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.getInventory().setHeldItemSlot(4);
        player.updateInventory();
    }
    
    public static PotionEffect getPotionEffect(final Player player, final PotionEffectType type) {
        Preconditions.checkState(player.hasPotionEffect(type));
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().getId() != type.getId()) {
                continue;
            }
            return effect;
        }
        throw new AssertionError();
    }
    
    public static void addConcideringLevel(final Player player, final PotionEffect effect) {
        if (player.hasPotionEffect(effect.getType())) {
            final PotionEffect before = getPotionEffect(player, effect.getType());
            if (before.getAmplifier() < effect.getAmplifier()) {
                player.addPotionEffect(effect, true);
            }
            else if (before.getAmplifier() == effect.getAmplifier() && before.getDuration() < effect.getDuration()) {
                player.addPotionEffect(effect, true);
            }
        }
        else {
            player.addPotionEffect(effect);
        }
    }
}
