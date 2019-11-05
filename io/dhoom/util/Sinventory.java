package io.dhoom.util;

import org.bukkit.entity.*;
import org.bukkit.*;
import com.google.common.collect.*;
import org.bukkit.inventory.*;
import java.util.*;
import io.dhoom.*;
import org.bukkit.event.inventory.*;
import io.dhoom.duel.*;
import io.dhoom.player.*;

public class Sinventory{
    private String title;
    private List<ItemStack> contents;
    private ItemStack oldPage;
    private ItemStack nextPage;
    private int page;
    private int totalPages;
    
    public Sinventory(final String title) {
        this.page = 1;
        this.totalPages = 1;
        this.title = title;
        this.contents = new ArrayList<ItemStack>();
        this.nextPage = UtilItem.createItem(Material.ARROW, 1, (short)0, ChatColor.GREEN + "Next ->");
        this.oldPage = UtilItem.createItem(Material.ARROW, 1, (short)0, ChatColor.GREEN + "<- Back");
    }
    
    public Sinventory addItem(final ItemStack item) {
        this.contents.add(item);
        this.setTotalPages();
        return this;
    }
    
    public void openInventory(final Player player) {
        if (this.totalPages == 1) {
            final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.calcSize(this.contents.size()), this.title);
            int slot = 0;
            for (final ItemStack item : this.contents) {
                inventory.setItem(slot++, item);
            }
            player.openInventory(inventory);
            return;
        }
        int startPoint = (this.page - 1) * 45;
        final List<ItemStack> invContents = (List<ItemStack>)Lists.newArrayList();
        try {
            ItemStack item2;
            while ((item2 = this.contents.get(startPoint++)) != null) {
                invContents.add(item2);
                if (startPoint - (this.page - 1) * 45 == 45) {
                    break;
                }
            }
        }
        catch (IndexOutOfBoundsException ex) {}
        final Inventory inventory2 = Bukkit.createInventory((InventoryHolder)null, 54, this.title);
        int slot2 = 0;
        for (final ItemStack invItem : invContents) {
            inventory2.setItem(slot2++, invItem);
        }
        if (this.page > 1) {
            inventory2.setItem(45, this.oldPage);
        }
        if (this.page < this.getPages(this.contents.size())) {
            inventory2.setItem(53, this.nextPage);
        }
        player.openInventory(inventory2);
    }
    
    public void executeClickEvent(final kPractice plugin, final Player player, final int slot, final InventoryClickEvent event) {
        event.setCancelled(true);
        if (slot == 45 && this.page > 1) {
            --this.page;
            this.openInventory(player);
        }
        if (slot == 53 && this.page < this.getPages(this.contents.size())) {
            ++this.page;
            this.openInventory(player);
        }
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().hasLore()) {
            final String duelIdentifier = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0).split(" ")[0]).replace("'s", "");
            if (Bukkit.getPlayer(duelIdentifier) == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ErrorMessages.player_not_found")));
                player.closeInventory();
                return;
            }
            final Player target = Bukkit.getPlayer(duelIdentifier);
            final Duel duel = plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(target.getUniqueId());
            if (duel == null) {
                player.sendMessage(ChatColor.RED + "That duel match has ended.");
                return;
            }
            final PracticePlayer practiceTarget = plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
            if (practiceTarget.getCurrentState() != PlayerState.FIGHTING) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("commands.spectate.no_fight")));
                return;
            }
            plugin.getManagerHandler().getSpectatorManager().addSpectator(player, target);
            player.sendMessage(ChatColor.YELLOW + "You are now spectating " + ChatColor.GREEN + target.getName());
        }
    }
    
    public List<ItemStack> getContents() {
        return this.contents;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    private void setTotalPages() {
        this.totalPages = ((this.contents.size() > 54) ? (this.contents.size() / 45) : 1);
    }
    
    private int calcSize(final int size) {
        return ((size - 1) / 9 + 1) * 9;
    }
    
    private int getPages(final int size) {
        if (size % 45 == 0) {
            return size / 45;
        }
        final Double value = (size + 1.0) / 45.0;
        return (int)Math.ceil(value);
    }
}
