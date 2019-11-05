package io.dhoom.player;

import org.bukkit.potion.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import io.kipes.util.*;
import java.util.*;

public class PlayerInventorySnapshot
{
    private String playerName;
    private UUID uuid;
    private ItemStack[] mainContent;
    private ItemStack[] armorContent;
    private Collection<PotionEffect> potionEffects;
    private int food;
    private double health;
    private Inventory inventory;
    
    public PlayerInventorySnapshot(final Player player) {
        this.playerName = player.getName();
        this.uuid = player.getUniqueId();
        this.mainContent = player.getInventory().getContents();
        this.armorContent = player.getInventory().getArmorContents();
        this.potionEffects = (Collection<PotionEffect>)player.getActivePotionEffects();
        this.food = player.getFoodLevel();
        this.health = player.getHealth();
        this.initInventory();
    }
    
    private void initInventory() {
        this.inventory = Bukkit.createInventory((InventoryHolder)null, 54, this.playerName + "'s inventory");
        final double roundedHealth = Math.round(this.health / 2.0 * 2.0) / 2.0;
        final ItemStack skull = UtilItem.createItem(Material.SKULL_ITEM, (int)Math.round(this.health), (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "\u2764 " + roundedHealth + " HP");
        this.inventory.setItem(44, skull);
        final ItemStack melon = UtilItem.createItem(Material.COOKED_BEEF, this.food, (short)0, ChatColor.GREEN.toString() + ChatColor.BOLD + "" + this.food + " Hunger");
        this.inventory.setItem(43, melon);
        final ArrayList<String> lores = new ArrayList<String>();
        for (final PotionEffect effect : this.potionEffects) {
            final int duration = effect.getDuration();
            final String durationMinuteSecond = UtilMath.convertTicksToMinutes(duration);
            final String effectAmplifierRoman = UtilString.romanNumerals(effect.getAmplifier() + 1);
            String effectName = effect.getType().getName().toLowerCase();
            effectName = effectName.replace('_', ' ');
            effectName = effectName.substring(0, 1).toUpperCase() + effectName.substring(1);
            lores.add(ChatColor.RESET + effectName + " " + effectAmplifierRoman + " (" + durationMinuteSecond + ")");
        }
        final ItemStack brewingStand = UtilItem.createItem(Material.BREWING_STAND_ITEM, this.potionEffects.size(), (short)0, ChatColor.GOLD.toString() + ChatColor.BOLD + "Potion Effects", lores);
        this.inventory.setItem(42, brewingStand);
        int healthPotAmount = 0;
        for (int i = 0; i < 36; ++i) {
            if (this.mainContent[i] != null && this.mainContent[i].getType() == Material.POTION) {
                if (this.mainContent[i].getDurability() == 16421) {
                    ++healthPotAmount;
                }
            }
        }
        if (healthPotAmount > 0) {
            final ItemStack healthPot = UtilItem.createItem(Material.POTION, healthPotAmount, (short)16421, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "" + healthPotAmount + " Health Pot(s)");
            this.inventory.setItem(49, healthPot);
        }
        final int soupAmount = (int)Arrays.stream(this.mainContent).filter(itemStack -> itemStack != null && itemStack.getType() == Material.MUSHROOM_SOUP).count();
        if (soupAmount > 0) {
            final ItemStack soup = UtilItem.createItem(Material.MUSHROOM_SOUP, soupAmount, (short)0, ChatColor.GREEN.toString() + ChatColor.BOLD + "" + soupAmount + " Soup Left");
            this.inventory.setItem(50, soup);
        }
        for (int i = 0; i < 36; ++i) {
            if (this.mainContent[i] != null) {
                this.inventory.setItem(i, this.mainContent[i]);
            }
        }
        for (int i = 36; i <= 39; ++i) {
            if (this.armorContent[39 - i] != null) {
                this.inventory.setItem(i, this.armorContent[39 - i]);
            }
        }
    }
    
    public Collection<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }
    
    public ItemStack[] getArmorContent() {
        return this.armorContent;
    }
    
    public ItemStack[] getMainContent() {
        return this.mainContent;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
}
