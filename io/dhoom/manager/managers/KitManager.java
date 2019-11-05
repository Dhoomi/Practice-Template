package io.dhoom.manager.managers;

import io.dhoom.kit.*;
import io.dhoom.manager.*;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;
import io.dhoom.player.*;
import org.bukkit.*;
import java.util.*;
import io.dhoom.util.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bson.*;
import com.mongodb.client.model.*;

public class KitManager extends Manager
{
    private Map<String, Kit> kitMap;
    private Config mainConfig;
    
    public KitManager(final ManagerHandler handler) {
        super(handler);
        this.kitMap = new HashMap<String, Kit>();
        this.mainConfig = new Config(handler.getPlugin(), "", "kit");
        this.loadKits();
        this.saveMongo();
    }
    
    public void disable() {
        this.saveKits();
    }
    
    public Map<String, Kit> getKitMap() {
        return this.kitMap;
    }
    
    public Kit getKit(final String kitName) {
        return this.getKitMap().get(kitName);
    }
    
    public Kit createKit(final String kitName) {
        final Kit kit = new Kit(kitName, null, false, true, true, new ItemStack[36], new ItemStack[36], false, false);
        this.kitMap.put(kitName, kit);
        return kit;
    }
    
    public void destroyKit(final String kitName) {
        this.kitMap.remove(kitName);
    }
    
    public void loadKits() {
        final FileConfiguration fileConfig = this.mainConfig.getConfig();
        final ConfigurationSection arenaSection = fileConfig.getConfigurationSection("kits");
        if (arenaSection == null) {
            return;
        }
        for (final String kitName : arenaSection.getKeys(false)) {
            final boolean enabled = arenaSection.getBoolean(kitName + ".enabled");
            final ItemStack icon = (ItemStack)arenaSection.get(kitName + ".icon");
            final boolean combo = arenaSection.getBoolean(kitName + ".combo");
            final boolean editable = arenaSection.getBoolean(kitName + ".editable");
            final boolean ranked = arenaSection.getBoolean(kitName + ".ranked");
            final boolean premium = arenaSection.getBoolean(kitName + ".premium");
            final boolean builduhc = arenaSection.getBoolean(kitName + ".builduhc");
            final ItemStack[] mainContents = ((List)arenaSection.get(kitName + ".mainContents")).toArray(new ItemStack[0]);
            final ItemStack[] armorContents = ((List)arenaSection.get(kitName + ".armorContents")).toArray(new ItemStack[0]);
            final Kit kit = new Kit(kitName, icon, combo, editable, ranked, mainContents, armorContents, premium, builduhc);
            kit.setEnabled(enabled);
            this.kitMap.put(kitName, kit);
        }
    }
    
    public void saveKits() {
        final FileConfiguration fileConfig = this.mainConfig.getConfig();
        fileConfig.set("kits", (Object)null);
        for (final Map.Entry<String, Kit> kitEntry : this.kitMap.entrySet()) {
            final String kitName = kitEntry.getKey();
            final Kit kit = kitEntry.getValue();
            if (kit.getIcon() != null && kit.getMainContents() != null) {
                if (kit.getArmorContents() == null) {
                    continue;
                }
                fileConfig.set("kits." + kitName + ".enabled", (Object)kit.isEnabled());
                fileConfig.set("kits." + kitName + ".icon", (Object)kit.getIcon());
                fileConfig.set("kits." + kitName + ".combo", (Object)kit.isCombo());
                fileConfig.set("kits." + kitName + ".editable", (Object)kit.isEditable());
                fileConfig.set("kits." + kitName + ".ranked", (Object)kit.isRanked());
                fileConfig.set("kits." + kitName + ".premium", (Object)kit.isPremium());
                fileConfig.set("kits." + kitName + ".mainContents", (Object)kit.getMainContents());
                fileConfig.set("kits." + kitName + ".armorContents", (Object)kit.getArmorContents());
                fileConfig.set("kits." + kitName + ".builduhc", (Object)kit.isBuilduhc());
            }
        }
        this.mainConfig.save();
    }
    
    public void openEditiKitsInventory(final Player player, final Kit defaultKit, final PlayerKit kit) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 54, "Editing Kit");
        inventory.setItem(0, UtilItem.createItem(Material.BOOK, 1, (short)0, ChatColor.YELLOW + "You are Editing (" + ChatColor.GREEN + defaultKit.getName() + " #" + kit.getKitIndex() + ChatColor.YELLOW + ")", Arrays.asList("", ChatColor.GRAY + "Kit Name:", kit.getDisplayName())));
        for (int i = 1; i <= 4; ++i) {
            inventory.setItem(i, UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)7, " "));
        }
        inventory.setItem(5, UtilItem.createItem(Material.STAINED_CLAY, 1, (short)13, ChatColor.GREEN.toString() + ChatColor.BOLD + "SAVE"));
        inventory.setItem(6, UtilItem.createItem(Material.STAINED_CLAY, 1, (short)14, ChatColor.RED.toString() + ChatColor.BOLD + "CANCEL"));
        inventory.setItem(7, UtilItem.createItem(Material.STAINED_CLAY, 1, (short)4, ChatColor.YELLOW.toString() + "Clear Inventory"));
        inventory.setItem(8, UtilItem.createItem(Material.STAINED_CLAY, 1, (short)0, ChatColor.GRAY.toString() + "Load Default Kit"));
        for (int i = 9; i <= 17; ++i) {
            inventory.setItem(i, UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)7, " "));
        }
        inventory.setItem(19, UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)7, " "));
        inventory.setItem(28, UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)7, " "));
        inventory.setItem(37, UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)7, " "));
        inventory.setItem(46, UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)7, " "));
        int i = 45;
        for (final ItemStack armor : defaultKit.getArmorContents()) {
            final ItemMeta meta = armor.getItemMeta();
            meta.setLore((List)Arrays.asList("", ChatColor.RED + "This is automatically equipped"));
            armor.setItemMeta(meta);
            inventory.setItem(i, armor);
            i -= 9;
        }
        for (final ItemStack item : defaultKit.getEditableContents()) {
            final int availableSlot = inventory.firstEmpty();
            if (availableSlot == -1) {
                continue;
            }
            inventory.setItem(availableSlot, item);
        }
        inventory.setItem(53, UtilItem.createItem(Material.POTION, 64, (short)16421, ChatColor.LIGHT_PURPLE + "Fill Empty Spots"));
        inventory.setItem(52, UtilItem.createItem(Material.BUCKET, 1, (short)0, ChatColor.RED + "Throw items here"));
        player.openInventory(inventory);
    }
    
    private void saveMongo() {
        for (final Kit kit : this.kitMap.values()) {
            final Document document = new Document();
            document.put("name", (Object)kit.getName());
            this.handler.getPlugin().getPracticeDatabase().getKits().replaceOne(Filters.eq("name", (Object)kit.getName()), (Object)document, new UpdateOptions().upsert(true));
        }
    }
}
