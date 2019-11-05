package io.dhoom.settings;

import io.dhoom.*;
import org.bukkit.plugin.*;
import java.util.*;
import com.google.common.collect.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.*;
import org.apache.commons.lang3.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class SettingsHandler implements CommandExecutor, Listener
{
    private final Practice plugin;
    
    public SettingsHandler(final Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        plugin.getCommand("setting").setExecutor((CommandExecutor)this);
    }
    
    public static ArrayList translate(final List<String> input) {
        return Lists.newArrayList((Iterable)Lists.transform((List)input, s -> ChatColor.translateAlternateColorCodes('&', (String)s)));
    }
    
    public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2, final String[] args) {
        this.open((Player)sender);
        return true;
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("Options")) {
            event.setCancelled(true);
            final Player player = (Player)event.getWhoClicked();
            final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
            final int slot = event.getSlot();
            if (slot == 1) {
                settings.setScoreboard(!settings.isScoreboard());
                player.sendMessage(ChatColor.YELLOW + "You are now " + this.able(settings.isScoreboard()) + ChatColor.YELLOW + " to see the scoreboard.");
            }
            else if (slot == 3) {
                settings.setDuelRequests(!settings.isDuelRequests());
                player.sendMessage(ChatColor.YELLOW + "You are now " + this.able(settings.isDuelRequests()) + ChatColor.YELLOW + " to receive duel requests.");
            }
            else if (slot == 5) {
                settings.setPublicChat(!settings.isPublicChat());
                player.sendMessage(ChatColor.YELLOW + "You are now " + this.able(settings.isPublicChat()) + ChatColor.YELLOW + " to see public chat messages.");
            }
            else if (slot == 7) {
                Bukkit.dispatchCommand((CommandSender)player, "toggletime");
            }
            this.open((Player)event.getWhoClicked());
        }
    }
    
    public String able(final boolean value) {
        return value ? (ChatColor.GREEN + "able") : (ChatColor.RED + "unable");
    }
    
    public void open(final Player player) {
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 9, "Options");
        final String enabled = ChatColor.BLUE + StringEscapeUtils.unescapeHtml4("&#9658;") + " ";
        ArrayList lore = Lists.newArrayList();
        lore.add("");
        lore.add(ChatColor.BLUE + "Do you want to see");
        lore.add(ChatColor.BLUE + "the scoreboard?");
        lore.add("");
        lore.add("  " + (settings.isScoreboard() ? enabled : " ") + ChatColor.YELLOW + "Show scoreboard");
        lore.add("  " + (settings.isScoreboard() ? " " : enabled) + ChatColor.YELLOW + "Hide scoreboard");
        String name = ChatColor.LIGHT_PURPLE + "Scoreboard";
        inventory.setItem(1, this.create(Material.PAINTING, name, lore));
        lore = Lists.newArrayList();
        lore.add("");
        lore.add(ChatColor.BLUE + "Do you want to receive");
        lore.add(ChatColor.BLUE + "duel requests?");
        lore.add("");
        lore.add("  " + (settings.isDuelRequests() ? enabled : " ") + ChatColor.YELLOW + "Show requests");
        lore.add("  " + (settings.isDuelRequests() ? " " : enabled) + ChatColor.YELLOW + "Hide requests");
        name = ChatColor.LIGHT_PURPLE + "Duel Requests";
        inventory.setItem(3, this.create(Material.SKULL_ITEM, (short)3, name, lore));
        lore = Lists.newArrayList();
        lore.add("");
        lore.add(ChatColor.BLUE + "Do you want to see");
        lore.add(ChatColor.BLUE + "public chat messages?");
        lore.add("");
        lore.add("  " + (settings.isPublicChat() ? enabled : " ") + ChatColor.YELLOW + "Show public chat");
        lore.add("  " + (settings.isPublicChat() ? " " : enabled) + ChatColor.YELLOW + "Hide public chat");
        name = ChatColor.LIGHT_PURPLE + "Public Chat";
        inventory.setItem(5, this.create(Material.SIGN, name, lore));
        lore = Lists.newArrayList();
        lore.add("");
        lore.add(ChatColor.BLUE + "Do you want to change");
        lore.add(ChatColor.BLUE + "daylight to nighttime?");
        lore.add("");
        lore.add("  " + (settings.isToggletime() ? enabled : " ") + ChatColor.YELLOW + "Show Night Time");
        lore.add("  " + (settings.isToggletime() ? " " : enabled) + ChatColor.YELLOW + "Show Day Time");
        name = ChatColor.LIGHT_PURPLE + "Toggle Time";
        inventory.setItem(7, this.create(Material.SLIME_BALL, name, lore));
        player.openInventory(inventory);
    }
    
    public ItemStack create(final Material material, final String name, final List<String> lore) {
        return this.create(material, (short)0, name, lore);
    }
    
    public ItemStack create(final Material material, final short data, final String name, final List<String> lore) {
        final ItemStack itemstack = new ItemStack(material, 1, data);
        final ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore((List)translate(lore));
        itemstack.setItemMeta(meta);
        return itemstack;
    }
}
