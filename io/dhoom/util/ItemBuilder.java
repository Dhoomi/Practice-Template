package io.dhoom.util;

import org.bukkit.inventory.*;
import org.bukkit.event.*;
import java.util.*;
import org.bukkit.material.*;
import org.bukkit.enchantments.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;

public class ItemBuilder extends ItemStack implements Listener{
    
    private final ItemStack is;
    
    public ItemBuilder(final Material mat) {
        this.is = new ItemStack(mat);
    }
    
    public ItemBuilder(final ItemStack is) {
        this.is = is.clone();
    }
    
    public ItemBuilder amount(final int amount) {
        this.is.setAmount(amount);
        return this;
    }
    
    public ItemBuilder name(final String name) {
        final ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(name);
        this.is.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder lore(final String name) {
        final ItemMeta meta = this.is.getItemMeta();
        List<String> lore = (List<String>)meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        }
        lore.add(name);
        meta.setLore((List)lore);
        this.is.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder lore(final List<String> lore) {
        final List<String> toSet = new ArrayList<String>();
        final ItemMeta meta = this.is.getItemMeta();
        for (final String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        meta.setLore((List)toSet);
        this.is.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder durability(final int durability) {
        this.is.setDurability((short)durability);
        return this;
    }
    
    public ItemBuilder data(final int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte)data));
        return this;
    }
    
    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }
    
    public ItemBuilder enchantment(final Enchantment enchantment) {
        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }
    
    public ItemBuilder type(final Material material) {
        this.is.setType(material);
        return this;
    }
    
    public ItemBuilder clearLore() {
        final ItemMeta meta = this.is.getItemMeta();
        meta.setLore((List)new ArrayList());
        this.is.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder clearEnchantments() {
        for (final Enchantment e : this.is.getEnchantments().keySet()) {
            this.is.removeEnchantment(e);
        }
        return this;
    }
    
    public ItemBuilder color(final Color color) {
        if (this.is.getType() == Material.LEATHER_BOOTS || this.is.getType() == Material.LEATHER_CHESTPLATE || this.is.getType() == Material.LEATHER_HELMET || this.is.getType() == Material.LEATHER_LEGGINGS) {
            final LeatherArmorMeta meta = (LeatherArmorMeta)this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta((ItemMeta)meta);
            return this;
        }
        throw new IllegalArgumentException("color() only applicable for leather armor!");
    }
    
    public ItemBuilder owner(final String owner) {
        final SkullMeta meta = (SkullMeta)this.is.getItemMeta();
        meta.setOwner(owner);
        this.is.setItemMeta((ItemMeta)meta);
        return this;
    }
    
    public ItemStack build() {
        return this.is;
    }
}
