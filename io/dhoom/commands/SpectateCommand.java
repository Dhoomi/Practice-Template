package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import io.dhoom.tournament.*;
import io.dhoom.player.*;
import java.util.*;

public class SpectateCommand implements CommandExecutor
{
    private Practice plugin;
    
    public SpectateCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
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
        if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
            this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player, true);
        }
        if (args.length == 0) {
            this.plugin.getManagerHandler().getSpectatorManager().toggleSpectatorMode(player);
            player.sendMessage(ChatColor.YELLOW + "You are now in spectator mode.");
        }
        else if (args.length == 1) {
            final Player target = this.plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.player_not_found")));
                return true;
            }
            final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
            if (practiceTarget.getCurrentState() != PlayerState.FIGHTING) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.spectate.no_fight")));
                return true;
            }
            if (!this.plugin.getManagerHandler().getSpectatorManager().isSpectatorMode(player)) {
                this.plugin.getManagerHandler().getSpectatorManager().toggleSpectatorMode(player);
            }
            this.plugin.getManagerHandler().getSpectatorManager().addSpectator(player, target);
        }
        else {
            player.sendMessage(ChatColor.RED + "Usage: /spectate (player)");
        }
        return true;
    }
}
