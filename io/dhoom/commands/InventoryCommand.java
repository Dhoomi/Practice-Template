package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;
import io.dhoom.player.*;

public class InventoryCommand implements CommandExecutor
{
    private Practice plugin;
    
    public InventoryCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        final Player player = (Player)sender;
        if (!args[0].matches(Practice.UUID_PATTER.pattern())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.inventory.snapshot")));
            return true;
        }
        final UUID invUUID = UUID.fromString(args[0]);
        final PlayerInventorySnapshot playerInventorySnapshot = this.plugin.getManagerHandler().getInventorySnapshotManager().getSnapshotFromUUID(invUUID);
        if (playerInventorySnapshot == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.inventory.snapshot")));
            return true;
        }
        player.openInventory(playerInventorySnapshot.getInventory());
        return true;
    }
}
