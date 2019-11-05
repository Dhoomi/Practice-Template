package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import io.dhoom.kit.*;

public class KitCommand implements CommandExecutor
{
    private Practice plugin;
    
    public KitCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] commandArgs) {
        if (commandArgs.length == 0) {
            final Player player = (Player)sender;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7*** &9Kit Help &7***"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit create &7- creates a kit"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit ranked &7- enable ranked for a kit"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit combo &7- set the kit as combo-only"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit sumo &7- set the kit as sumo-only"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit builduhc &7- set the kit as builduhc-only"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit enbale&7- enable the kit for use"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit disable&7- disable the kit for use"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit remove&7- remove the kit completely"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit setinventory&7- set the default kit layout"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/kit seticon&7- set the kit icon (in queue GUI)"));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.console_only")));
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.commands.kit")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.nopermission")));
            return true;
        }
        if (commandArgs[0].equalsIgnoreCase("create")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit create <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            if (this.plugin.getManagerHandler().getKitManager().getKit(kitName) != null) {
                sender.sendMessage(ChatColor.RED + "This kit already exists!");
                return true;
            }
            this.plugin.getManagerHandler().getKitManager().createKit(kitName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.create").replace("%kit%", kitName)));
        }
        else if (commandArgs[0].equalsIgnoreCase("combo")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit combo <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setCombo(!kit.isCombo());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.combo").replace("%kit%", kitName).replace("%combo%", kit.isCombo() ? "combo mode" : "not combo mode")));
        }
        else if (commandArgs[0].equalsIgnoreCase("ranked")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit ranked <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setRanked(!kit.isRanked());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.ranked").replace("%kit%", kitName).replace("%ranked%", kit.isRanked() ? "ranked mode" : "not ranked mode")));
        }
        else if (commandArgs[0].equalsIgnoreCase("premium")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit premium <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setPremium(!kit.isPremium());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.premium").replace("%kit%", kitName).replace("%premium%", kit.isPremium() ? "premium mode" : "non premium mode")));
        }
        else if (commandArgs[0].equalsIgnoreCase("editable")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit editable <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setEditable(!kit.isEditable());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.editable").replace("%kit%", kitName).replace("%editable%", kit.isEditable() ? "editable" : "not editable")));
        }
        else if (commandArgs[0].equalsIgnoreCase("builduhc")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit builduhc <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setBuilduhc(!kit.isBuilduhc());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.builduhc").replace("%kit%", kitName).replace("%builduhc%", kit.isBuilduhc() ? "builduhc mode" : "not builduhc mode")));
        }
        else if (commandArgs[0].equalsIgnoreCase("enable")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit enable <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setEnabled(!kit.isEnabled());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.enable").replace("%kit%", kitName).replace("%enable%", kit.isEnabled() ? "enabled" : "disabled")));
        }
        else if (commandArgs[0].equalsIgnoreCase("setinventory")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit setinventory <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setMainContents(player.getInventory().getContents());
            kit.setArmorContents(player.getInventory().getArmorContents());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.setinventory").replace("%kit%", kitName)));
        }
        else if (commandArgs[0].equalsIgnoreCase("retrieve")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit retrieve <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            player.getInventory().setContents(kit.getMainContents());
            player.getInventory().setArmorContents(kit.getArmorContents());
            player.updateInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.retrieve").replace("%kit%", kitName)));
        }
        else if (commandArgs[0].equalsIgnoreCase("seticon")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit seticon <kit name>");
                return true;
            }
            if (player.getItemInHand() == null) {
                sender.sendMessage(ChatColor.RED + "You need something in your hand!");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setIcon(player.getItemInHand());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.seticon").replace("%kit%", kitName)));
        }
        else {
            if (!commandArgs[0].equalsIgnoreCase("remove")) {
                player.sendMessage(ChatColor.RED + "Incorrect usage");
                return true;
            }
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /kit remove <arena name>");
                return true;
            }
            final String kitName = commandArgs[1];
            if (this.plugin.getManagerHandler().getKitManager().getKit(kitName) == null) {
                player.sendMessage(ChatColor.RED + "This kit does not exist!");
                return true;
            }
            this.plugin.getManagerHandler().getKitManager().destroyKit(kitName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.kit.remove").replace("%kit%", kitName)));
        }
        this.plugin.getManagerHandler().getInventoryManager().setUnrankedInventory();
        this.plugin.getManagerHandler().getInventoryManager().setRankedInventory();
        this.plugin.getManagerHandler().getInventoryManager().setPremiumInventory();
        this.plugin.getManagerHandler().getInventoryManager().setEditorInventory();
        this.plugin.getManagerHandler().getInventoryManager().setRequestInventory();
        this.plugin.getManagerHandler().getInventoryManager().setSplitFightInventory();
        this.plugin.getManagerHandler().getInventoryManager().setFfaPartyInventory();
        return true;
    }
}
