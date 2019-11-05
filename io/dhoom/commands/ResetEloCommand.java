package io.dhoom.commands;

import io.dhoom.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import io.dhoom.kit.*;
import io.dhoom.player.*;
import java.util.*;

public class ResetEloCommand implements CommandExecutor
{
    private Practice plugin;
    
    public ResetEloCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
            return true;
        }
        if (practicePlayer.getCredits() <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.reset.no_reset")));
            return true;
        }
        practicePlayer.setCredits(practicePlayer.getCredits() - 1);
        for (final Kit kit : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
            practicePlayer.addElo(kit.getName(), 1000);
            practicePlayer.addPartyElo(practicePlayer.getUUID(), kit.getName(), 1000);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("commands.reset.reset")));
        return true;
    }
}
