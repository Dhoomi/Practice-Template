package io.dhoom.manager.managers;

import org.bukkit.entity.*;
import io.dhoom.*;
import io.dhoom.manager.*;
import io.dhoom.arena.*;
import io.dhoom.kit.*;
import io.dhoom.scoreboard.provider.sidebar.*;
import io.dhoom.util.*;
import org.bukkit.event.*;
import org.bukkit.inventory.*;
import io.dhoom.scoreboard.*;
import io.dhoom.party.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.potion.*;
import io.dhoom.player.*;
import io.dhoom.duel.*;
import io.dhoom.events.*;
import io.dhoom.runnables.other.*;
import org.bukkit.plugin.*;
import java.util.*;

public class DuelManager extends Manager
{
    public ArrayList<Player> sumo;
    private Practice plugin;
    private Map<UUID, Duel> uuidIdentifierToDuel;
    private Map<UUID, UUID> playerUUIDToDuelUUID;
    private Random random;
    
    public DuelManager(final Practice plugin, final ManagerHandler handler) {
        super(handler);
        this.sumo = new ArrayList<Player>();
        this.plugin = plugin;
        this.uuidIdentifierToDuel = new HashMap<UUID, Duel>();
        this.playerUUIDToDuelUUID = new HashMap<UUID, UUID>();
        this.random = new Random();
    }
    
    public Duel getDuelByUUID(final UUID uuid) {
        return this.uuidIdentifierToDuel.get(uuid);
    }
    
    public void createDuel(final Arena arena, final Kit kit, final UUID ffaPartyLeaderUUID, final List<UUID> ffaPlayers) {
        final UUID matchUUID = UUID.randomUUID();
        final Duel duel = new Duel(arena.getName(), kit.getName(), matchUUID, ffaPartyLeaderUUID, ffaPlayers);
        this.uuidIdentifierToDuel.put(matchUUID, duel);
        final List<Player> duelPlayers = new ArrayList<Player>();
        boolean lastLocation = false;
        final ItemStack defaultBook = UtilItem.createItem(Material.BOOK, 1, (short)1, ChatColor.GREEN + "KIT: Default");
        for (final UUID uuid : ffaPlayers) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
            if (playerBoard != null) {
                playerBoard.setDefaultSidebar(new DuelScoreboardProvider(this.handler.getPlugin()), 2L);
            }
            UtilPlayer.clear(player);
            this.playerUUIDToDuelUUID.put(player.getUniqueId(), matchUUID);
            if (lastLocation) {
                lastLocation = false;
                player.teleport(arena.getSecondTeamLocation());
            }
            else {
                lastLocation = true;
                player.teleport(arena.getFirstTeamLocation());
            }
            final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(player);
            final String kitName = kit.getName();
            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kitName);
            player.getInventory().setItem(0, defaultBook);
            if (playerKitMap != null) {
                int i = 2;
                for (final PlayerKit playerKit : playerKitMap.values()) {
                    player.getInventory().setItem(i, UtilItem.createItem(Material.BOOK, 1, (short)0, playerKit.getDisplayName()));
                    ++i;
                }
            }
            player.updateInventory();
            practicePlayer.setCurrentState(PlayerState.WAITING);
            player.sendMessage(ChatColor.YELLOW + "Starting ffa duel against party");
            duelPlayers.add(player);
        }
        if (ffaPartyLeaderUUID != null) {
            final Party party = this.handler.getPartyManager().getParty(ffaPartyLeaderUUID);
            if (party != null) {
                party.setPartyState(PartyState.DUELING);
            }
        }
        for (final Player player2 : duelPlayers) {
            for (final Player player3 : duelPlayers) {
                player2.showPlayer(player3);
            }
        }
        duelPlayers.clear();
        this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelCreateEvent(duel));
    }
    
    public void createDuel(final Arena arena, final Kit kit, final boolean ranked, final UUID firstTeamPartyLeaderUUID, final UUID secondTeamPartyLeaderUUID, final List<UUID> firstTeam, final List<UUID> secondTeam, final boolean tournament, final boolean premium) {
        final UUID matchUUID = UUID.randomUUID();
        final Duel duel = new Duel(arena.getName(), kit.getName(), matchUUID, ranked, firstTeamPartyLeaderUUID, secondTeamPartyLeaderUUID, firstTeam, secondTeam, tournament, premium);
        if (kit.getName().equalsIgnoreCase("Sumo")) {
            for (final UUID firstteam : firstTeam) {
                Practice.getInstance().getManagerHandler().getDuelManager().sumo.add(Bukkit.getPlayer(firstteam));
            }
            for (final UUID secondteam : secondTeam) {
                Practice.getInstance().getManagerHandler().getDuelManager().sumo.add(Bukkit.getPlayer(secondteam));
            }
        }
        this.uuidIdentifierToDuel.put(matchUUID, duel);
        final List<Player> duelPlayers = new ArrayList<Player>();
        final ItemStack defaultBook = UtilItem.createItem(Material.BOOK, 1, (short)1, ChatColor.GREEN + "KIT: Default");
        String firstTeamRanked = "";
        String secondTeamRanked = "";
        if (ranked) {
            if (firstTeam.size() == 1 && secondTeam.size() == 1) {
                firstTeamRanked = ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer((UUID)firstTeam.get(0)).getName() + ChatColor.YELLOW + " with " + ChatColor.GREEN + "" + this.handler.getPracticePlayerManager().getPracticePlayer(firstTeam.get(0)).getEloMap().get(kit.getName()) + " elo";
                secondTeamRanked = ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer((UUID)secondTeam.get(0)).getName() + ChatColor.YELLOW + " with " + ChatColor.GREEN + "" + this.handler.getPracticePlayerManager().getPracticePlayer(secondTeam.get(0)).getEloMap().get(kit.getName()) + " elo";
            }
            else if (firstTeam.size() == 2 && secondTeam.size() == 2 && firstTeamPartyLeaderUUID != null && secondTeamPartyLeaderUUID != null) {
                final Party firstTeamParty = this.handler.getPartyManager().getParty(firstTeamPartyLeaderUUID);
                final Party secondTeamParty = this.handler.getPartyManager().getParty(secondTeamPartyLeaderUUID);
                firstTeamRanked = ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer(firstTeamPartyLeaderUUID).getName() + ChatColor.YELLOW + ", " + ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer((UUID)firstTeamParty.getMembers().get(0)).getName() + ChatColor.YELLOW + " with " + ChatColor.GREEN + this.handler.getPracticePlayerManager().getPracticePlayer(firstTeamPartyLeaderUUID).getPartyEloMap().get(firstTeamParty.getMembers().get(0)).get(kit.getName()) + " elo";
                secondTeamRanked = ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer(secondTeamPartyLeaderUUID).getName() + ChatColor.YELLOW + ", " + ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer((UUID)secondTeamParty.getMembers().get(0)).getName() + ChatColor.YELLOW + " with " + ChatColor.GREEN + this.handler.getPracticePlayerManager().getPracticePlayer(secondTeamPartyLeaderUUID).getPartyEloMap().get(secondTeamParty.getMembers().get(0)).get(kit.getName()) + " elo";
            }
        }
        for (final UUID uuid : firstTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            UtilPlayer.clear(player);
            this.playerUUIDToDuelUUID.put(player.getUniqueId(), matchUUID);
            player.teleport(arena.getFirstTeamLocation());
            final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(player);
            final String kitName = kit.getName();
            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kitName);
            player.getInventory().setItem(0, defaultBook);
            if (playerKitMap != null && !duel.isTournament()) {
                int i = 2;
                for (final PlayerKit playerKit : playerKitMap.values()) {
                    player.getInventory().setItem(i, UtilItem.createItem(Material.BOOK, 1, (short)0, playerKit.getDisplayName()));
                    ++i;
                }
            }
            player.updateInventory();
            practicePlayer.setCurrentState(PlayerState.WAITING);
            practicePlayer.setTeamNumber(1);
            if (kit.isCombo()) {
                player.setNoDamageTicks(0);
                player.setMaximumNoDamageTicks(2);
                Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), "knockback set combo " + player.getName());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 0));
            }
            final boolean party = secondTeam.size() >= 2;
            practicePlayer.setShowRematchItemFlag(true);
            practicePlayer.setLastDuelPlayer(this.handler.getPlugin().getServer().getPlayer((UUID)secondTeam.get(0)).getName());
            player.setSaturation(11.0f);
            player.sendMessage(ChatColor.YELLOW + "Starting duel against " + (ranked ? secondTeamRanked : (ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer((UUID)secondTeam.get(0)).getName())) + (party ? (ChatColor.YELLOW + "'s party.") : ""));
            duelPlayers.add(player);
        }
        for (final UUID uuid : secondTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            UtilPlayer.clear(player);
            this.playerUUIDToDuelUUID.put(player.getUniqueId(), matchUUID);
            player.teleport(arena.getSecondTeamLocation());
            final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(player);
            final String kitName = kit.getName();
            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kitName);
            player.getInventory().setItem(0, defaultBook);
            if (playerKitMap != null && !duel.isTournament()) {
                int i = 2;
                for (final PlayerKit playerKit : playerKitMap.values()) {
                    player.getInventory().setItem(i, UtilItem.createItem(Material.BOOK, 1, (short)0, playerKit.getDisplayName()));
                    ++i;
                }
            }
            player.updateInventory();
            practicePlayer.setCurrentState(PlayerState.WAITING);
            practicePlayer.setTeamNumber(2);
            if (kit.isCombo()) {
                player.setNoDamageTicks(0);
                player.setMaximumNoDamageTicks(2);
                Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), "knockback set combo " + player.getName());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 0));
            }
            final boolean party = firstTeam.size() >= 2;
            practicePlayer.setShowRematchItemFlag(true);
            practicePlayer.setLastDuelPlayer(this.handler.getPlugin().getServer().getPlayer((UUID)firstTeam.get(0)).getName());
            player.setSaturation(11.0f);
            player.sendMessage(ChatColor.YELLOW + "Starting duel against " + (ranked ? firstTeamRanked : (ChatColor.GREEN + this.handler.getPlugin().getServer().getPlayer((UUID)firstTeam.get(0)).getName())) + (party ? (ChatColor.YELLOW + "'s party.") : ""));
            duelPlayers.add(player);
        }
        if (firstTeamPartyLeaderUUID != null) {
            final Party party2 = this.handler.getPartyManager().getParty(firstTeamPartyLeaderUUID);
            if (party2 != null) {
                party2.setPartyState(PartyState.DUELING);
            }
        }
        if (secondTeamPartyLeaderUUID != null) {
            final Party party2 = this.handler.getPartyManager().getParty(secondTeamPartyLeaderUUID);
            if (party2 != null) {
                party2.setPartyState(PartyState.DUELING);
            }
        }
        for (final Player player2 : duelPlayers) {
            for (final Player player3 : duelPlayers) {
                player2.showPlayer(player3);
            }
        }
        for (final UUID uuid : firstTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
            if (playerBoard == null) {
                continue;
            }
            playerBoard.setDefaultSidebar(new DuelScoreboardProvider(this.handler.getPlugin()), 2L);
        }
        for (final UUID uuid : secondTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
            if (playerBoard == null) {
                continue;
            }
            playerBoard.setDefaultSidebar(new DuelScoreboardProvider(this.handler.getPlugin()), 2L);
        }
        for (final Player playerInDuel : duelPlayers) {
            this.handler.getScoreboardHandler().getPlayerBoard(playerInDuel.getUniqueId());
        }
        duelPlayers.clear();
        this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelCreateEvent(duel));
    }
    
    public Duel getDuelFromPlayer(final UUID uuid) {
        final UUID matchUUID = this.playerUUIDToDuelUUID.get(uuid);
        return this.uuidIdentifierToDuel.get(matchUUID);
    }
    
    public void removePlayerFromDuel(final Player player) {
        final Duel currentDuel = this.getDuelFromPlayer(player.getUniqueId());
        this.playerUUIDToDuelUUID.remove(player.getUniqueId());
        if (currentDuel == null) {
            return;
        }
        final PlayerBoard playerBoard = Practice.getInstance().getManagerHandler().getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        final PlayerInventorySnapshot playerInventorySnapshot = new PlayerInventorySnapshot(player);
        currentDuel.addSnapshot(player.getUniqueId(), playerInventorySnapshot);
        if (currentDuel.getFfaPlayers() != null) {
            currentDuel.killPlayerFFA(player.getUniqueId());
            for (final UUID uuid : currentDuel.getFfaPlayers()) {
                final Player other = Bukkit.getPlayer(uuid);
                if (other != null) {
                    if (player.getKiller() != null) {
                        other.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has been killed by " + ChatColor.GREEN + player.getKiller().getName() + ChatColor.YELLOW + ".");
                    }
                    else {
                        other.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has been killed" + ChatColor.YELLOW + ".");
                    }
                }
            }
            if (currentDuel.getFfaPlayersAlive().size() == 1) {
                final Player lastPlayer = this.handler.getPlugin().getServer().getPlayer((UUID)currentDuel.getFfaPlayersAlive().get(0));
                final PlayerInventorySnapshot lastPlayerSnapshot = new PlayerInventorySnapshot(lastPlayer);
                final UUID lastPlayerSnapUUID = UUID.randomUUID();
                currentDuel.addUUIDSnapshot(lastPlayer.getUniqueId(), lastPlayerSnapUUID);
                this.handler.getInventorySnapshotManager().addSnapshot(lastPlayerSnapUUID, lastPlayerSnapshot);
                for (final Map.Entry<UUID, PlayerInventorySnapshot> entry : currentDuel.getPlayerUUIDToSnapshotMap().entrySet()) {
                    final UUID playerUUID = entry.getKey();
                    final PlayerInventorySnapshot pSnapshot = entry.getValue();
                    final UUID snapUUID = UUID.randomUUID();
                    currentDuel.addUUIDSnapshot(playerUUID, snapUUID);
                    this.handler.getInventorySnapshotManager().addSnapshot(snapUUID, pSnapshot);
                }
                currentDuel.setDuelState(DuelState.ENDING);
                currentDuel.setEndMatchTime(System.currentTimeMillis());
                this.playerUUIDToDuelUUID.remove(currentDuel.getFfaPlayersAlive().get(0));
                this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelEndingEvent(currentDuel));
            }
            return;
        }
        for (final UUID uuid : currentDuel.getFirstTeam()) {
            final Player other = Bukkit.getPlayer(uuid);
            if (other != null) {
                if (player.getKiller() != null) {
                    other.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has been killed by " + ChatColor.GREEN + player.getKiller().getName() + ChatColor.YELLOW + ".");
                }
                else {
                    other.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has been killed" + ChatColor.YELLOW + ".");
                }
            }
        }
        for (final UUID uuid : currentDuel.getSecondTeam()) {
            final Player other = Bukkit.getPlayer(uuid);
            if (other != null) {
                if (player.getKiller() != null) {
                    other.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has been killed by " + ChatColor.GREEN + player.getKiller().getName() + ChatColor.YELLOW + ".");
                }
                else {
                    other.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has been killed" + ChatColor.YELLOW + ".");
                }
            }
        }
        final int teamNumber = this.handler.getPracticePlayerManager().getPracticePlayer(player).getTeamNumber();
        if (teamNumber == 1) {
            currentDuel.killPlayerFirstTeam(player.getUniqueId());
        }
        else {
            currentDuel.killPlayerSecondTeam(player.getUniqueId());
        }
        if (currentDuel.getFirstTeamAlive().size() == 0) {
            for (final UUID lastPlayersUUID : currentDuel.getSecondTeamAlive()) {
                final Player lastPlayers = this.handler.getPlugin().getServer().getPlayer(lastPlayersUUID);
                final PlayerInventorySnapshot lastPlayerSnapshot2 = new PlayerInventorySnapshot(lastPlayers);
                currentDuel.addSnapshot(lastPlayers.getUniqueId(), lastPlayerSnapshot2);
            }
            for (final Map.Entry<UUID, PlayerInventorySnapshot> entry2 : currentDuel.getPlayerUUIDToSnapshotMap().entrySet()) {
                final UUID playerUUID2 = entry2.getKey();
                final PlayerInventorySnapshot pSnapshot2 = entry2.getValue();
                final UUID snapUUID2 = UUID.randomUUID();
                currentDuel.addUUIDSnapshot(playerUUID2, snapUUID2);
                this.handler.getInventorySnapshotManager().addSnapshot(snapUUID2, pSnapshot2);
            }
            currentDuel.setDuelState(DuelState.ENDING);
            currentDuel.setEndMatchTime(System.currentTimeMillis());
            this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelEndingEvent(currentDuel, 2));
            this.playerUUIDToDuelUUID.remove(currentDuel.getSecondTeamAlive().get(0));
        }
        else if (currentDuel.getSecondTeamAlive().size() == 0) {
            for (final UUID lastPlayersUUID : currentDuel.getFirstTeamAlive()) {
                final Player lastPlayers = this.handler.getPlugin().getServer().getPlayer(lastPlayersUUID);
                final PlayerInventorySnapshot lastPlayerSnapshot2 = new PlayerInventorySnapshot(lastPlayers);
                currentDuel.addSnapshot(lastPlayers.getUniqueId(), lastPlayerSnapshot2);
            }
            for (final Map.Entry<UUID, PlayerInventorySnapshot> entry2 : currentDuel.getPlayerUUIDToSnapshotMap().entrySet()) {
                final UUID playerUUID2 = entry2.getKey();
                final PlayerInventorySnapshot pSnapshot2 = entry2.getValue();
                final UUID snapUUID2 = UUID.randomUUID();
                currentDuel.addUUIDSnapshot(playerUUID2, snapUUID2);
                this.handler.getInventorySnapshotManager().addSnapshot(snapUUID2, pSnapshot2);
            }
            currentDuel.setDuelState(DuelState.ENDING);
            currentDuel.setEndMatchTime(System.currentTimeMillis());
            this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelEndingEvent(currentDuel, 1));
            this.playerUUIDToDuelUUID.remove(currentDuel.getFirstTeamAlive().get(0));
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.RANKED_SOLO));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.RANKED_PARTY));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.UNRANKED_SOLO));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.PREMIUM_SOLO));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.SPECTATOR));
    }
    
    public void destroyDuel(final Duel duel) {
        this.uuidIdentifierToDuel.remove(duel.getUUID());
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.RANKED_SOLO));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.RANKED_PARTY));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.UNRANKED_SOLO));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.PREMIUM_SOLO));
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.SPECTATOR));
    }
    
    public int getRankedDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && duel.isRanked()) {
                ++count;
            }
        }
        return count;
    }
    
    public int getRankedPartyDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && duel.isRanked() && duel.getFirstTeam().size() >= 2) {
                ++count;
            }
        }
        return count;
    }
    
    public int getUnRankedDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && !duel.isRanked()) {
                ++count;
            }
        }
        return count;
    }
    
    public int getPremiumDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && duel.isPremium()) {
                ++count;
            }
        }
        return count;
    }
    
    public boolean isSumo(final Player player) {
        return this.sumo.contains(player);
    }
    
    public int getUnRankedPartyDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && !duel.isRanked() && duel.getFirstTeam().size() >= 2) {
                ++count;
            }
        }
        return count;
    }
    
    public Duel getRandomDuel() {
        final List<Duel> list = new ArrayList<Duel>(this.uuidIdentifierToDuel.values());
        Collections.shuffle(list);
        final Duel duel = list.get(this.random.nextInt(list.size()));
        if (duel != null) {
            return duel;
        }
        return null;
    }
    
    public Map<UUID, Duel> getUuidIdentifierToDuel() {
        return this.uuidIdentifierToDuel;
    }
    
    public Map<UUID, UUID> getPlayerUUIDToDuelUUID() {
        return this.playerUUIDToDuelUUID;
    }
}
