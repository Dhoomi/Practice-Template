package io.dhoom.util;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.enchantments.*;
import java.util.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;

public class UtilItem
{
    public static ItemStack createItem(final Material m, final int amount) {
        return new ItemStack(m, amount);
    }
    
    public static ItemStack createItem(final Material m, final int amount, final short durability) {
        return new ItemStack(m, amount, durability);
    }
    
    public static ItemStack createItem(final Material m, final int amount, final short durability, final String name) {
        ItemStack itemStack = new ItemStack(m, amount, durability);
        if (name != null) {
            itemStack = name(itemStack, name);
        }
        return itemStack;
    }
    
    public static ItemStack createItem(final Material m, final int amount, final short durability, final String name, final Enchantment enchantment, final int level) {
        ItemStack itemStack = new ItemStack(m, amount, durability);
        if (name != null) {
            itemStack = name(itemStack, name);
        }
        return enchantItem(itemStack, enchantment, level);
    }
    
    public static ItemStack createItem(final Material m, final int amount, final short durability, final String name, final List<String> lore) {
        ItemStack itemStack = new ItemStack(m, amount, durability);
        if (name != null) {
            itemStack = name(itemStack, name);
        }
        if (lore != null) {
            itemStack = lore(itemStack, lore);
        }
        return itemStack;
    }
    
    public static ItemStack skull(final ItemStack itemStack, final String playerName) {
        final SkullMeta meta = (SkullMeta)itemStack.getItemMeta();
        meta.setOwner(playerName);
        itemStack.setItemMeta((ItemMeta)meta);
        return itemStack;
    }
    
    public static ItemStack name(final ItemStack itemStack, final String itemName) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public static ItemStack lore(final ItemStack itemStack, final List<String> lore) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore((List)lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public static ItemStack enchantItem(final ItemStack itemStack, final Enchantment enchantment, final int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return itemStack;
    }
    
    public static ItemStack effectItem(final ItemStack itemStack, final PotionEffectType potionEffectType, final int time, final int amplifier) {
        final PotionMeta potionMeta = (PotionMeta)itemStack.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(potionEffectType, time * 20, amplifier), true);
        itemStack.setItemMeta((ItemMeta)potionMeta);
        return itemStack;
    }
    
    public static String getTitle(final ItemStack itemStack) {
        final ItemMeta meta;
        if (itemStack.hasItemMeta() && (meta = itemStack.getItemMeta()).hasDisplayName()) {
            return meta.getDisplayName();
        }
        return "";
    }
}
