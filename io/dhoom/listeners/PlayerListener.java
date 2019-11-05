package io.dhoom.listeners;

import io.dhoom.*;
import java.text.*;
import org.bukkit.event.*;
import org.bukkit.potion.*;
import org.bukkit.entity.*;
import io.dhoom.duel.*;
import org.bukkit.*;
import io.dhoom.runnables.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.*;
import org.bukkit.event.block.*;
import io.dhoom.tournament.*;
import org.bukkit.command.*;
import io.dhoom.party.*;
import io.dhoom.player.*;
import org.bukkit.block.*;
import io.dhoom.kit.*;
import org.bukkit.event.entity.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import io.dhoom.util.*;
import org.bukkit.event.player.*;
import java.lang.reflect.*;

public class PlayerListener implements Listener
{
    private static Map<UUID, Long> lastPearl;
    private Practice plugin;
    private DecimalFormat formatter;
    
    public PlayerListener(final Practice plugin) {
        this.formatter = new DecimalFormat("0.0");
        PlayerListener.lastPearl = new HashMap<UUID, Long>();
        this.plugin = plugin;
    }
    
    public static Map<UUID, Long> getLastPearl() {
        return PlayerListener.lastPearl;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent e) {
        this.plugin.getManagerHandler().getPracticePlayerManager().createPracticePlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
        final ItemStack itemStack = event.getItem();
        if (itemStack.getType() == Material.GOLDEN_APPLE && ChatColor.stripColor(UtilItem.getTitle(itemStack)).equalsIgnoreCase(ChatColor.GOLD + "&6Golden Head")) {
            final PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 200, 1);
            UtilPlayer.addConcideringLevel(event.getPlayer(), effect);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBuildUHCArrow(final EntityDamageByEntityEvent event) {
        final Projectile projectile;
        if (event.getDamager() instanceof Arrow && (projectile = (Projectile)event.getDamager()).getShooter() instanceof Player && event.getEntity() instanceof Player) {
            final Player player = (Player)projectile.getShooter();
            final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(event.getEntity().getUniqueId());
            if (duel != null && this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
                final Player target = (Player)event.getEntity();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.buildarrow").replace("%name%", target.getName()).replace("%hp%", this.formatter.format((target.getHealth() - event.getFinalDamage()) / 2.0))));
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setDamage(100.0);
        }
    }
    
    @EventHandler
    public void onPlayerRegainHealth(final EntityRegainHealthEvent event) {
        final Duel duel;
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && event.getEntity() instanceof Player && (duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(event.getEntity().getUniqueId())) != null && this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer != null) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party != null) {
                if (party.getLeader().equals(player.getUniqueId())) {
                    this.plugin.getManagerHandler().getPartyManager().destroyParty(player.getUniqueId());
                    for (final UUID member : party.getMembers()) {
                        final Player pLayer = this.plugin.getServer().getPlayer(member);
                        pLayer.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.leader_left")));
                        final PracticePlayer ppLayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pLayer);
                        if (ppLayer.getCurrentState() != PlayerState.LOBBY) {
                            continue;
                        }
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(pLayer);
                    }
                    this.plugin.getManagerHandler().getInventoryManager().delParty(party);
                }
                else {
                    this.plugin.getManagerHandler().getPartyManager().leaveParty(player.getUniqueId());
                    this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
                }
            }
            switch (practicePlayer.getCurrentState()) {
                case WAITING:
                case FIGHTING: {
                    this.plugin.getManagerHandler().getDuelManager().removePlayerFromDuel(e.getPlayer());
                    break;
                }
                case QUEUE: {
                    if (party == null) {
                        this.plugin.getManagerHandler().getQueueManager().unqueueSingleQueue(e.getPlayer().getUniqueId());
                        break;
                    }
                    for (final UUID uuid : party.getMembers()) {
                        final Player memberPlayer = this.plugin.getServer().getPlayer(uuid);
                        if (memberPlayer == null) {
                            continue;
                        }
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(memberPlayer);
                    }
                    this.plugin.getManagerHandler().getQueueManager().unqueuePartyQueue(party.getLeader());
                    this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.leave")));
                    final Player leaderPlayer = this.plugin.getServer().getPlayer(party.getLeader());
                    if (leaderPlayer == null) {
                        break;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(leaderPlayer);
                    break;
                }
                case EDITING: {
                    this.plugin.getManagerHandler().getEditorManager().removeEditingKit(e.getPlayer().getUniqueId());
                    break;
                }
                case SPECTATING: {
                    this.plugin.getManagerHandler().getSpectatorManager().removeSpectatorMode(player);
                    break;
                }
            }
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously((Plugin)Practice.getInstance(), (Runnable)new RemovePlayerTask(e.getPlayer()));
        this.plugin.getManagerHandler().getPracticePlayerManager().removePracticePlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerInteractSoup(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (!p.isDead() && p.getItemInHand().getType() == Material.MUSHROOM_SOUP && p.getHealth() < 19.0) {
            final double newHealth = (p.getHealth() + 7.0 > 20.0) ? 20.0 : (p.getHealth() + 7.0);
            p.setHealth(newHealth);
            p.getItemInHand().setType(Material.BOWL);
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntityEvent(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player && e.getPlayer().getItemInHand().getType() == Material.FLOWER_POT) {
            e.getPlayer().openInventory((Inventory)((Player)e.getRightClicked()).getInventory());
        }
    }
    
    @EventHandler
    public void onPlayerPreCommand(final PlayerCommandPreprocessEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() == PlayerState.LOADING) {
            e.getPlayer().sendMessage(ChatColor.RED + "Your information is loading, if your information doesn't load, please contact a staff member.");
            e.setCancelled(true);
        }
        else if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
            e.getPlayer().sendMessage(ChatColor.RED + "You can't execute commands while editing a kit.");
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerJoinEvent e) {
        e.setJoinMessage((String)null);
        new BukkitRunnable() {
            public void run() {
                final PracticePlayer practicePlayer = PlayerListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
                PlayerListener.this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(e.getPlayer());
            }
        }.runTaskLater((Plugin)this.plugin, 20L);
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerQuitEvent e) {
        e.setQuitMessage((String)null);
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() == Material.DIAMOND_SWORD) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            final Player player = (Player)e.getEntity().getShooter();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            practicePlayer.getProjectileQueue().add(e.getEntity());
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            final Player player = e.getPlayer();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            final ItemStack interactedItem = e.getItem();
            final Block interactedBlock = e.getClickedBlock();
            if (practicePlayer.getCurrentState() == PlayerState.LOADING) {
                e.getPlayer().sendMessage(ChatColor.RED + "Your information is loading, if your information doesn't load, please contact a staff member.");
                return;
            }
            if (interactedItem != null) {
                if (practicePlayer.getCurrentState() == PlayerState.LOBBY) {
                    if (Tournament.getTournaments().size() > 0) {
                        for (final Tournament tournament : Tournament.getTournaments()) {
                            if (!tournament.isInTournament(player)) {
                                continue;
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ErrorMessages.cannot_perform")));
                            return;
                        }
                    }
                    if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                        switch (interactedItem.getType()) {
                            case NAME_TAG: {
                                this.plugin.getServer().dispatchCommand((CommandSender)player, "party info");
                                break;
                            }
                            case NETHER_STAR: {
                                this.plugin.getServer().dispatchCommand((CommandSender)player, "party leave");
                                break;
                            }
                            case BOOK: {
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditorInventory());
                                break;
                            }
                            case SKULL_ITEM: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.not_leader")));
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.is_busy")));
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getPartiesInventory());
                                break;
                            }
                            case GOLD_AXE: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.not_leader")));
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.is_busy")));
                                    break;
                                }
                                if (party.getMembers().size() == 0) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.inventory.must_be_two")));
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getPartyEventsInventory());
                                break;
                            }
                            case IRON_SWORD: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.not_leader")));
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.is_busy")));
                                    break;
                                }
                                if (party.getSize() != 2) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.inventory.must_be_two")));
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory());
                                break;
                            }
                            case DIAMOND_SWORD: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.not_leader")));
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.is_busy")));
                                    break;
                                }
                                if (party.getSize() != 2) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.inventory.must_be_two")));
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRankedInventory());
                                break;
                            }
                        }
                    }
                    else {
                        switch (interactedItem.getType()) {
                            case WATCH: {
                                player.chat("/settings");
                                break;
                            }
                            case BLAZE_POWDER: {
                                player.chat("/duel " + practicePlayer.getLastDuelPlayer());
                                break;
                            }
                            case IRON_SWORD: {
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory());
                                break;
                            }
                            case EMERALD: {
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditorInventory());
                                break;
                            }
                            case DIAMOND_SWORD: {
                                if (practicePlayer.getUnrankedWins() < 20 && !player.isOp()) {
                                    player.sendMessage(ChatColor.RED + "You must win 20 Unranked Matches to play in Ranked Matches.");
                                    player.sendMessage(ChatColor.RED + "You need (" + (20 - practicePlayer.getUnrankedWins()) + ") more win" + ((practicePlayer.getUnrankedWins() == 19) ? "" : "s") + " to play.");
                                    return;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRankedInventory());
                                break;
                            }
                            case GOLD_SWORD: {
                                if (practicePlayer.getPremiumTokens() <= 0 && !player.hasPermission("practice.premium.bypass")) {
                                    player.sendMessage(ChatColor.RED + "You don't have enough Premium Matches.");
                                    player.sendMessage(ChatColor.RED + "You currently have 0 matches.");
                                    return;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getPremiumInventory());
                                break;
                            }
                            case BOOK: {
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditorInventory());
                                break;
                            }
                            case NETHER_STAR: {
                                this.plugin.getServer().dispatchCommand((CommandSender)player, "party create");
                                break;
                            }
                            case FLOWER_POT: {
                                this.plugin.getServer().dispatchCommand((CommandSender)player, "spectate");
                                break;
                            }
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.QUEUE) {
                    switch (interactedItem.getType()) {
                        case REDSTONE: {
                            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                            if (party != null) {
                                for (final UUID uuid : party.getMembers()) {
                                    final Player memberPlayer = this.plugin.getServer().getPlayer(uuid);
                                    if (memberPlayer == null) {
                                        continue;
                                    }
                                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(memberPlayer);
                                }
                                this.plugin.getManagerHandler().getQueueManager().unqueuePartyQueue(party.getLeader());
                                this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.party_left")));
                                final Player leaderPlayer = this.plugin.getServer().getPlayer(party.getLeader());
                                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(leaderPlayer);
                                break;
                            }
                            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                            this.plugin.getManagerHandler().getQueueManager().unqueueSingleQueue(player.getUniqueId());
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.left")));
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) {
                    switch (interactedItem.getType()) {
                        case BOOK: {
                            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId()).getKitName());
                            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kit.getName());
                            for (int i = 0; i < 9; ++i) {
                                final ItemStack item = player.getInventory().getItem(i);
                                if (item != null) {
                                    if (item.equals((Object)interactedItem) && i == 0) {
                                        player.getInventory().setContents(kit.getMainContents());
                                        player.getInventory().setArmorContents(kit.getArmorContents());
                                        player.updateInventory();
                                        break;
                                    }
                                    if (item.equals((Object)interactedItem)) {
                                        final PlayerKit playerKit = playerKitMap.get(i - 1);
                                        if (playerKit != null) {
                                            player.getInventory().setContents(playerKit.getMainContents());
                                            player.getInventory().setArmorContents(playerKit.getArmorContents());
                                            player.updateInventory();
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        case ENDER_PEARL: {
                            if (practicePlayer.getCurrentState() != PlayerState.FIGHTING) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("listener.player.enderpearl")));
                                e.setCancelled(true);
                                break;
                            }
                            final long now = System.currentTimeMillis();
                            final double d;
                            final double diff = d = (PlayerListener.lastPearl.containsKey(player.getUniqueId()) ? (now - PlayerListener.lastPearl.get(player.getUniqueId())) : ((double)now));
                            if (diff < 15000.0) {
                                player.sendMessage(ChatColor.RED + "Pearl cooldown: " + ChatColor.GRAY + new DecimalFormat("#.#").format(15.0 - diff / 1000.0) + " seconds");
                                e.setCancelled(true);
                                player.updateInventory();
                                break;
                            }
                            PlayerListener.lastPearl.put(player.getUniqueId(), now);
                            break;
                        }
                        case POTION: {
                            if (interactedItem.getAmount() <= 1) {
                                break;
                            }
                            e.setCancelled(true);
                            interactedItem.setAmount(1);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't use stacked potions."));
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
                    switch (interactedItem.getType()) {
                        case REDSTONE: {
                            this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player, true);
                            break;
                        }
                        case NETHER_STAR: {
                            this.plugin.getManagerHandler().getSpectatorManager().removeSpectatorMode(player);
                            break;
                        }
                        case EMERALD: {
                            this.plugin.getManagerHandler().getInventoryManager().getSpectatorInventory().openInventory(player);
                            break;
                        }
                        case PAPER: {
                            if (this.plugin.getManagerHandler().getDuelManager().getUuidIdentifierToDuel().values().size() == 0) {
                                player.sendMessage(ChatColor.RED + "There are currently no matches to spectate.");
                                return;
                            }
                            final Duel duel = this.plugin.getManagerHandler().getDuelManager().getRandomDuel();
                            if (duel == null) {
                                player.sendMessage(ChatColor.RED + "Please try again, an error occured.");
                                return;
                            }
                            final Player spectator = Bukkit.getPlayer((UUID)duel.getFirstTeam().get(0));
                            if (spectator == null) {
                                player.sendMessage(ChatColor.RED + "Please try again, an error occured.");
                                return;
                            }
                            this.plugin.getManagerHandler().getSpectatorManager().addSpectator(player, spectator);
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
                    switch (interactedItem.getType()) {
                        case POTION: {
                            e.setCancelled(true);
                            break;
                        }
                        case ENDER_PEARL: {
                            e.setCancelled(true);
                            break;
                        }
                    }
                }
            }
            if (interactedBlock != null && practicePlayer.getCurrentState() == PlayerState.EDITING) {
                switch (interactedBlock.getType()) {
                    case SIGN_POST:
                    case WALL_SIGN:
                    case SIGN: {
                        this.plugin.getManagerHandler().getEditorManager().removeEditingKit(player.getUniqueId());
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                        break;
                    }
                    case CHEST: {
                        e.setCancelled(true);
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitItemsInventory(player.getUniqueId()));
                        break;
                    }
                    case ANVIL: {
                        e.setCancelled(true);
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitKitsInventory(player.getUniqueId()));
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        final Iterator recipents = e.getRecipients().iterator();
        while (recipents.hasNext()) {
            final PracticePlayer practicePlayer = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(recipents.next());
            if (practicePlayer.getSettings().isPublicChat()) {
                continue;
            }
            recipents.remove();
        }
        final PlayerKit kitRenaming = this.plugin.getManagerHandler().getEditorManager().getKitRenaming(e.getPlayer().getUniqueId());
        if (kitRenaming != null) {
            kitRenaming.setDisplayName(e.getMessage().replaceAll("&", "�"));
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Successfully set kit " + ChatColor.GREEN + kitRenaming.getKitIndex() + ChatColor.YELLOW + "'s name to " + ChatColor.GREEN + kitRenaming.getDisplayName());
            this.plugin.getManagerHandler().getEditorManager().removeRenamingKit(e.getPlayer().getUniqueId());
            e.getPlayer().openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitKitsInventory(e.getPlayer().getUniqueId()));
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() != PlayerState.FIGHTING && practicePlayer.getCurrentState() != PlayerState.WAITING && practicePlayer.getCurrentState() != PlayerState.BUILDER) {
            e.setCancelled(true);
        }
        if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && e.getItemDrop().getItemStack().getType() == Material.BOOK) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() != PlayerState.FIGHTING && practicePlayer.getCurrentState() != PlayerState.WAITING && practicePlayer.getCurrentState() != PlayerState.BUILDER) {
            e.setCancelled(true);
        }
        if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && e.getItem().getItemStack().getType() == Material.BOOK) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player)e.getEntity();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            if (practicePlayer != null && (practicePlayer.getCurrentState() != PlayerState.FIGHTING || player.getInventory().contains(Material.MUSHROOM_SOUP))) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        PlayerListener.lastPearl.remove(player.getUniqueId());
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        System.out.println(practicePlayer.getCurrentState());
        if (practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) {
            this.plugin.getManagerHandler().getDuelManager().removePlayerFromDuel(player);
        }
        if (e.getEntity() instanceof CraftPlayer) {
            PlayerUtility.getOnlinePlayers().stream().filter(p -> p.canSee(player)).forEach(p -> {
                if (p instanceof CraftPlayer) {}
                return;
            });
            this.autoRespawn(e);
        }
        e.setDeathMessage((String)null);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(e.getPlayer()), 2L);
    }
    
    private void autoRespawn(final PlayerDeathEvent e) {
        new BukkitRunnable() {
            public void run() {
                try {
                    final Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(e.getEntity(), new Object[0]);
                    final Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    final Class EntityPlayer2 = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");
                    final Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    final Object mcserver = minecraftServer.get(con);
                    final Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList", (Class<?>[])new Class[0]).invoke(mcserver, new Object[0]);
                    final Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer2, Integer.TYPE, Boolean.TYPE);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater((Plugin)this.plugin, 2L);
    }
}
