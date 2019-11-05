package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import io.dhoom.tournament.*;
import io.dhoom.lang.*;
import org.bukkit.*;
import io.dhoom.util.*;
import io.dhoom.player.*;
import java.util.*;
import io.dhoom.party.*;

public class PartyCommand implements CommandExecutor
{
    private Practice plugin;
    
    public PartyCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7*** &2Party Help &7***"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party create &7- creates a party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party help &7- show this message"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party info &7- view party information"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party leave &7- leave your party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party open &7- open the party to public"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party lock &7- lock party (invite only)"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party join &7- join a party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party kick &7- kick member from party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party invite &7- invite player to party"));
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
            return true;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (!tournament.isInTournament(player)) {
                    continue;
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7*** &2Party Help &7***"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party create &7- creates a party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party help &7- show this message"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party info &7- view party information"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party leave &7- leave your party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party open &7- open the party to public"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party lock &7- lock party (invite only)"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party join &7- join a party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party kick &7- kick member from party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party invite &7- invite player to party"));
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.already_in_party")));
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().createParty(player.getUniqueId(), player.getName());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.create")));
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            this.plugin.getManagerHandler().getInventoryManager().addParty(party);
        }
        else if (args[0].equalsIgnoreCase("info")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.no_party")));
                return true;
            }
            final Player leader = this.plugin.getServer().getPlayer(party.getLeader());
            final StringJoiner members = new StringJoiner(", ");
            for (final UUID memberUUID : party.getMembers()) {
                final Player member = this.plugin.getServer().getPlayer(memberUUID);
                members.add(member.getName());
            }
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "PARTY_INFO_COMMAND").replace("%party_leader%", leader.getName()).replace("%party_size%", String.valueOf(party.getMembers().size() + 1)).replace("%party_members%", members.toString()).replace("%party_state%", party.isOpen() ? "Open" : "Locked"));
        }
        else if (args[0].equalsIgnoreCase("leave")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.no_party")));
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                this.plugin.getManagerHandler().getPartyManager().destroyParty(player.getUniqueId());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.disband")));
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                for (final UUID member2 : party.getMembers()) {
                    final Player pLayer = this.plugin.getServer().getPlayer(member2);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.disbanded")));
                    final PracticePlayer ppLayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pLayer);
                    if (ppLayer.getCurrentState() != PlayerState.LOBBY) {
                        continue;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(pLayer);
                }
                this.plugin.getManagerHandler().getInventoryManager().delParty(party);
            }
            else {
                this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.leave").replace("%player%", player.getName())));
                this.plugin.getManagerHandler().getPartyManager().leaveParty(player.getUniqueId());
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
            }
        }
        else if (args[0].equalsIgnoreCase("open")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.no_party")));
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                if (party.isOpen()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.opened")));
                    return true;
                }
                party.setOpen(true);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.open")));
            }
            else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.not_leader")));
            }
        }
        else if (args[0].equalsIgnoreCase("lock")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.no_party")));
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                if (!party.isOpen()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.locked_already")));
                    return true;
                }
                party.setOpen(false);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.locked")));
            }
            else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.not_leader")));
            }
        }
        else if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party join <player>");
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.already_in_party")));
                return true;
            }
            final Player target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_not_found")));
                return true;
            }
            final Party party2 = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
            if (party2 == null || !party2.getLeader().equals(target.getUniqueId())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.exist")));
                return true;
            }
            if (!party2.isOpen()) {
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(player)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.no_requests")));
                    return true;
                }
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(player, target)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.no_requests")));
                    return true;
                }
                this.plugin.getManagerHandler().getRequestManager().removePartyRequest(player, target);
            }
            if (party2.getMembers().size() >= 15) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.full")));
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().joinParty(party2.getLeader(), player.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party2, ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.join").replace("%player%", player.getName())));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.party_join")));
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party2);
        }
        else if (args[0].equalsIgnoreCase("kick")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party kick <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.no_party")));
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.not_leader")));
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_not_found")));
                return true;
            }
            if (party.getLeader() == target2.getUniqueId()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.cannot_kick_leader")));
                return true;
            }
            if (!party.getMembers().contains(target2.getUniqueId())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.not_in_party")));
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().leaveParty(target2.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.kick_party").replace("%player%", target2.getName())));
            target2.sendMessage(ChatColor.YELLOW + "You were kicked from the party.");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.kicked")));
            final PracticePlayer ptarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target2);
            if (ptarget.getCurrentState() == PlayerState.LOBBY) {
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(target2);
            }
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "PLAYER_KICK_PARTY_TWO").replace("%player%", target2.getName()));
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
        }
        else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party invite <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.no_party")));
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.not_leader")));
                return true;
            }
            if (party.isOpen()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.is_opened")));
                return true;
            }
            if (party.getMembers().size() >= 15) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.full")));
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_not_found")));
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(target2.getUniqueId()) != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.in_party")));
                return true;
            }
            if (this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(target2) && this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(target2, player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.party.invitation")));
                return true;
            }
            player.sendMessage(ChatColor.YELLOW + "Sent a party request to " + ChatColor.GREEN + target2.getName() + ChatColor.YELLOW + "!");
            this.plugin.getManagerHandler().getRequestManager().addPartyRequest(target2, player);
            final UtilActionMessage actionMessage = new UtilActionMessage();
            actionMessage.addText(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has invited you to their party! ");
            actionMessage.addText("" + ChatColor.RED + "[Click here to accept]").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/party join " + player.getName());
            actionMessage.sendToPlayer(target2);
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7*** &2Party Help &7***"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party create &7- creates a party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party help &7- show this message"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party info &7- view party information"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party leave &7- leave your party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party open &7- open the party to public"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party lock &7- lock party (invite only)"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party join &7- join a party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party kick &7- kick member from party"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/party invite &7- invite player to party"));
        }
        return true;
    }
}
