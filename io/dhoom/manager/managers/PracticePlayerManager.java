package io.dhoom.manager.managers;

import io.dhoom.manager.*;
import io.dhoom.player.*;
import io.dhoom.runnables.*;
import org.bukkit.plugin.*;
import io.dhoom.listeners.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import io.dhoom.util.*;
import io.dhoom.scoreboard.provider.sidebar.*;
import io.dhoom.scoreboard.*;
import io.dhoom.kit.*;
import java.util.*;

public class PracticePlayerManager extends Manager
{
    public PracticePlayerManager(final ManagerHandler handler) {
        super(handler);
    }
    
    public void disable() {
        for (final PracticePlayer profile : PracticePlayer.getProfiles()) {
            profile.save();
        }
        for (final Player player : PlayerUtility.getOnlinePlayers()) {
            new SavePlayerConfig(player.getUniqueId(), this.handler.getPlugin()).run();
        }
    }
    
    private void loadPlayerData(final PracticePlayer player) {
        player.setCurrentState(PlayerState.LOADING);
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new LoadPlayerTask(this.handler.getPlugin(), player));
    }
    
    public void createPracticePlayer(final Player player) {
        final PracticePlayer practicePlayer = new PracticePlayer(player.getUniqueId(), true);
        this.sendToLobby(player);
        this.loadPlayerData(practicePlayer);
    }
    
    public PracticePlayer getPracticePlayer(final Player player) {
        return PracticePlayer.getByUuid(player.getUniqueId());
    }
    
    public PracticePlayer getPracticePlayer(final UUID uuid) {
        return PracticePlayer.getByUuid(uuid);
    }
    
    public void removePracticePlayer(final Player player) {
        this.handler.getPlugin().getServer().getScheduler().runTaskAsynchronously((Plugin)this.handler.getPlugin(), (Runnable)new SavePlayerConfig(player.getUniqueId(), this.handler.getPlugin()));
    }
    
    public void sendToLobby(final Player player) {
        PlayerListener.getLastPearl().remove(player.getUniqueId());
        for (final Player onlinePlayer : PlayerUtility.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
            player.showPlayer(onlinePlayer);
        }
        UtilPlayer.clear(player);
        final PracticePlayer practicePlayer = PracticePlayer.getByUuid(player.getUniqueId());
        if (practicePlayer == null) {
            return;
        }
        Projectile current;
        while ((current = practicePlayer.getProjectileQueue().poll()) != null) {
            current.remove();
        }
        practicePlayer.setCurrentState(PlayerState.LOBBY);
        practicePlayer.setTeamNumber(0);
        if (this.handler.getPlugin().getSpawn() != null) {
            player.teleport(this.handler.getPlugin().getSpawn());
        }
        if (this.handler.getPartyManager().getParty(player.getUniqueId()) != null) {
            player.getInventory().setContents(this.handler.getItemManager().getPartyItems());
            player.updateInventory();
        }
        else if (this.handler.getSpectatorManager().isSpectatorMode(player)) {
            player.getInventory().setContents(this.handler.getItemManager().getSpectatorModeItems());
            player.updateInventory();
            practicePlayer.setCurrentState(PlayerState.SPECTATING);
        }
        else {
            player.getInventory().setContents(this.handler.getItemManager().getSpawnItems());
            if (practicePlayer.isShowRematchItemFlag()) {
                practicePlayer.setShowRematchItemFlag(false);
                final ItemStack itemStack = new ItemStack(Material.BLAZE_POWDER);
                UtilItem.name(itemStack, ChatColor.GOLD + "Request Rematch");
                player.getInventory().setItem(3, itemStack);
            }
            player.updateInventory();
        }
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        if (playerBoard != null) {
            playerBoard.setDefaultSidebar(new LobbyScoreboardProvider(this.handler.getPlugin()), 2L);
            playerBoard.setSidebarVisible(practicePlayer.isScoreboard());
        }
    }
    
    public void returnItems(final Player player) {
        final PracticePlayer practicePlayer;
        Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)this.handler.getPlugin(), () -> {
            UtilPlayer.clear(player);
            practicePlayer = PracticePlayer.getByUuid(player.getUniqueId());
            if (practicePlayer != null) {
                practicePlayer.setCurrentState(PlayerState.LOBBY);
                practicePlayer.setTeamNumber(0);
                if (this.handler.getPartyManager().getParty(player.getUniqueId()) != null) {
                    player.getInventory().setContents(this.handler.getItemManager().getPartyItems());
                    player.updateInventory();
                }
                else {
                    player.getInventory().setContents(this.handler.getItemManager().getSpawnItems());
                    player.updateInventory();
                }
            }
        }, 1L);
    }
    
    public int getGlobalElo(final PracticePlayer player, final boolean solo) {
        int i = 0;
        int count = 0;
        for (final Kit kit : this.handler.getKitManager().getKitMap().values()) {
            if (solo) {
                i += player.getEloMap().get(kit.getName());
                ++count;
            }
            else {
                if (!player.getPartyEloMap().containsKey(player.getUUID())) {
                    continue;
                }
                if (!player.getPartyEloMap().get(player.getUUID()).containsKey(kit.getName())) {
                    continue;
                }
                i += player.getPartyEloMap().get(player.getUUID()).get(kit.getName());
                ++count;
            }
        }
        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }
        return Math.round(i / count);
    }
    
    public int getPremiumElo(final PracticePlayer player) {
        int i = 0;
        int count = 0;
        for (final Kit kit : this.handler.getKitManager().getKitMap().values()) {
            if (!kit.isPremium()) {
                continue;
            }
            i += player.getPremiumEloMap().get(kit.getName());
            ++count;
        }
        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }
        return Math.round(i / count);
    }
}
