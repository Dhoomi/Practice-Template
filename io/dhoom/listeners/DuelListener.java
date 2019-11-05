package io.dhoom.listeners;

import io.dhoom.*;
import io.dhoom.arena.*;
import io.dhoom.kit.*;
import org.bukkit.entity.*;
import com.google.common.collect.*;
import org.bukkit.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.scoreboard.*;
import java.lang.reflect.*;
import org.bukkit.scheduler.*;
import io.dhoom.duel.*;
import org.bukkit.event.*;
import io.dhoom.player.*;
import org.bukkit.plugin.*;
import io.dhoom.runnables.other.*;
import io.dhoom.events.*;
import io.dhoom.tournament.*;
import io.dhoom.util.*;
import org.bukkit.command.*;
import java.util.*;
import io.dhoom.scoreboard.*;
import io.dhoom.party.*;

public class DuelListener implements Listener
{
    private final String NO_ARENA_AVAILABLE;
    private Practice plugin;
    private Map<UUID, BukkitTask> duelTickThreadMap;
    private Map<UUID, Integer> duelCountdownMap;
    
    public DuelListener(final Practice plugin) {
        this.NO_ARENA_AVAILABLE = ChatColor.RED + "There are no arenas available at this moment";
        this.duelTickThreadMap = new HashMap<UUID, BukkitTask>();
        this.duelCountdownMap = new HashMap<UUID, Integer>();
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDuelPreCreate(final DuelPreCreateEvent e) {
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().getRandomArena();
        final Kit kit = e.getKit();
        if (arena == null || kit == null) {
            for (final UUID uuid : e.getFirstTeam()) {
                final Player player = this.plugin.getServer().getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                player.sendMessage(this.NO_ARENA_AVAILABLE);
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            }
            for (final UUID uuid : e.getSecondTeam()) {
                final Player player = this.plugin.getServer().getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                player.sendMessage(this.NO_ARENA_AVAILABLE);
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            }
        }
        else if (e.getFfaPlayers() != null) {
            this.plugin.getManagerHandler().getDuelManager().createDuel(arena, kit, e.getFfaPartyLeaderUUID(), e.getFfaPlayers());
        }
        else {
            this.plugin.getManagerHandler().getDuelManager().createDuel(arena, kit, e.isRanked(), e.getFirstTeamPartyLeaderUUID(), e.getSecondTeamPartyLeaderUUID(), e.getFirstTeam(), e.getSecondTeam(), false, e.isPremium());
        }
    }
    
    public void unSetHealthBars(final Duel duel) {
        final List<Player> allPlayers = (List<Player>)Lists.newArrayList();
        if (duel.getFirstTeam() != null) {
            for (final UUID uuid : duel.getFirstTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getSecondTeam() != null) {
            for (final UUID uuid : duel.getSecondTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getFfaPlayers() != null) {
            for (final UUID uuid : duel.getFfaPlayers()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        for (final Player player2 : allPlayers) {
            final Scoreboard sb = player2.getScoreboard();
            if (sb == Bukkit.getScoreboardManager().getMainScoreboard()) {
                continue;
            }
            final Objective objective = sb.getObjective("showhealth");
            if (objective == null) {
                continue;
            }
            objective.unregister();
        }
    }
    
    public void setHealthBars(final Duel duel) {
        final List<Player> allPlayers = (List<Player>)Lists.newArrayList();
        if (duel.getFirstTeam() != null) {
            for (final UUID uuid : duel.getFirstTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getSecondTeam() != null) {
            for (final UUID uuid : duel.getSecondTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getFfaPlayers() != null) {
            for (final UUID uuid : duel.getFfaPlayers()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        for (final Player player2 : allPlayers) {
            final Scoreboard sb = player2.getScoreboard();
            if (sb == Bukkit.getScoreboardManager().getMainScoreboard()) {
                continue;
            }
            Objective objective = sb.getObjective("showhealth");
            if (objective != null) {
                continue;
            }
            try {
                final Field field = EntityPlayer.class.getDeclaredField("bP");
                field.setAccessible(true);
                for (final Player other : allPlayers) {
                    field.setFloat(((CraftPlayer)other).getHandle(), Float.MIN_VALUE);
                }
            }
            catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
            objective = sb.registerNewObjective("showhealth", "health");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.RED + "\u2764");
        }
    }
    
    @EventHandler
    public void onDuelCreate(final DuelCreateEvent e) {
        final Duel duel = e.getDuel();
        this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName()).setOpen(false);
        if (this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
            this.setHealthBars(duel);
        }
        this.duelCountdownMap.put(duel.getUUID(), 6);
        final BukkitTask tickThread = new BukkitRunnable() {
            public void run() {
                int countdown = DuelListener.this.duelCountdownMap.get(duel.getUUID());
                if (duel.getDuelState() == DuelState.STARTING) {
                    if (--countdown == 0) {
                        duel.setDuelState(DuelState.FIGHTING);
                        if (duel.getFfaPlayers() != null) {
                            for (final UUID uuid : duel.getFfaPlayersAlive()) {
                                final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                                if (player == null) {
                                    continue;
                                }
                                final PracticePlayer practicePlayer = DuelListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
                                practicePlayer.setCurrentState(PlayerState.FIGHTING);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', DuelListener.this.plugin.getConfig().getString("listener.duel.start")));
                            }
                        }
                        else {
                            for (final UUID uuid : duel.getFirstTeamAlive()) {
                                final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                                if (player == null) {
                                    continue;
                                }
                                final PracticePlayer practicePlayer = DuelListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
                                practicePlayer.setCurrentState(PlayerState.FIGHTING);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', DuelListener.this.plugin.getConfig().getString("listener.duel.start")));
                            }
                            for (final UUID uuid : duel.getSecondTeamAlive()) {
                                final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                                if (player == null) {
                                    continue;
                                }
                                final PracticePlayer practicePlayer = DuelListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
                                practicePlayer.setCurrentState(PlayerState.FIGHTING);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', DuelListener.this.plugin.getConfig().getString("listener.duel.start")));
                            }
                        }
                        return;
                    }
                    if (duel.getFfaPlayers() != null) {
                        for (final UUID uuid : duel.getFfaPlayersAlive()) {
                            final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                            if (player == null) {
                                continue;
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', DuelListener.this.plugin.getConfig().getString("listener.duel.starting").replace("%countdown%", String.valueOf(countdown))));
                        }
                    }
                    else {
                        for (final UUID uuid : duel.getFirstTeamAlive()) {
                            final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                            if (player == null) {
                                continue;
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', DuelListener.this.plugin.getConfig().getString("listener.duel.starting").replace("%countdown%", String.valueOf(countdown))));
                        }
                        for (final UUID uuid : duel.getSecondTeamAlive()) {
                            final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                            if (player == null) {
                                continue;
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', DuelListener.this.plugin.getConfig().getString("listener.duel.starting").replace("%countdown%", String.valueOf(countdown))));
                        }
                    }
                }
                else if (duel.getDuelState() == DuelState.ENDING) {
                    countdown -= 2;
                    if (countdown <= 0) {
                        DuelListener.this.plugin.getServer().getPluginManager().callEvent((Event)new DuelEndEvent(duel));
                    }
                }
                DuelListener.this.duelCountdownMap.put(duel.getUUID(), countdown);
            }
        }.runTaskTimer((Plugin)this.plugin, 20L, 20L);
        duel.setStartMatchTime(System.currentTimeMillis());
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.SPECTATOR));
        this.duelTickThreadMap.put(duel.getUUID(), tickThread);
    }
    
    @EventHandler
    public void onDuelEnd(final DuelEndingEvent e) {
        final Duel duel = e.getDuel();
        this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName()).setOpen(true);
        if (this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
            this.unSetHealthBars(duel);
        }
        this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName()).getBlockChangeTracker().rollback();
        this.duelCountdownMap.put(duel.getUUID(), 6);
        final UtilActionMessage winnerMessage = new UtilActionMessage();
        final UtilActionMessage loserMessage = new UtilActionMessage();
        final Set<Player> duelPlayers = new HashSet<Player>();
        if (duel.getFfaPlayers() != null) {
            final Player lastPlayer = this.plugin.getServer().getPlayer((UUID)duel.getFfaPlayersAlive().get(0));
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(lastPlayer);
            practicePlayer.setCurrentState(PlayerState.WAITING);
            winnerMessage.addText(ChatColor.GREEN + lastPlayer.getName() + " ").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(lastPlayer.getUniqueId()));
            for (final UUID uuidPlayer : duel.getFfaPlayers()) {
                if (uuidPlayer.equals(lastPlayer.getUniqueId())) {
                    continue;
                }
                final Player player = this.plugin.getServer().getPlayer(uuidPlayer);
                if (player == null) {
                    continue;
                }
                duelPlayers.add(player);
                loserMessage.addText(ChatColor.RED + player.getName() + " ").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(player.getUniqueId()));
            }
            duelPlayers.add(lastPlayer);
        }
        else {
            List<UUID> winningTeam = null;
            List<UUID> losingTeam = null;
            final int teamNumber = e.getTeamNumber();
            switch (teamNumber) {
                case 1: {
                    winningTeam = duel.getFirstTeam();
                    losingTeam = duel.getSecondTeam();
                    break;
                }
                case 2: {
                    winningTeam = duel.getSecondTeam();
                    losingTeam = duel.getFirstTeam();
                    break;
                }
            }
            for (final UUID uuidPlayer2 : duel.getFirstTeamAlive()) {
                final Player player2 = this.plugin.getServer().getPlayer(uuidPlayer2);
                if (player2 == null) {
                    continue;
                }
                final PracticePlayer practicePlayer2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player2);
                practicePlayer2.setCurrentState(PlayerState.WAITING);
            }
            for (final UUID uuidPlayer2 : duel.getSecondTeamAlive()) {
                final Player player2 = this.plugin.getServer().getPlayer(uuidPlayer2);
                if (player2 == null) {
                    continue;
                }
                final PracticePlayer practicePlayer2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player2);
                practicePlayer2.setCurrentState(PlayerState.WAITING);
            }
            if (duel.isTournament() && Tournament.getTournaments().size() > 0) {
                for (final Tournament tournament : Tournament.getTournaments()) {
                    if (tournament != null) {
                        final Iterator<TournamentMatch> iterator = tournament.getCurrentMatches().iterator();
                        while (iterator.hasNext()) {
                            final TournamentMatch match = iterator.next();
                            if (match.getFirstTeam().getPlayers().equals(duel.getFirstTeam()) && match.getSecondTeam().getPlayers().equals(duel.getSecondTeam())) {
                                final String winningTeamOne = ChatColor.YELLOW.toString() + ChatColor.BOLD + "\u272a " + ChatColor.GREEN + Bukkit.getOfflinePlayer(duel.getFirstTeamPartyLeaderUUID()).getName() + ((duel.getFirstTeam().size() > 1) ? "'s Team" : "") + ChatColor.GOLD + " has eliminated " + ChatColor.RED + Bukkit.getOfflinePlayer(duel.getSecondTeamPartyLeaderUUID()).getName() + ((duel.getSecondTeam().size() > 1) ? "'s Team" : "");
                                final String winningTeamTwo = ChatColor.YELLOW.toString() + ChatColor.BOLD + "\u272a " + ChatColor.GREEN + Bukkit.getOfflinePlayer(duel.getSecondTeamPartyLeaderUUID()).getName() + ((duel.getSecondTeam().size() > 1) ? "'s Team" : "") + ChatColor.GOLD + " has eliminated " + ChatColor.RED + Bukkit.getOfflinePlayer(duel.getFirstTeamPartyLeaderUUID()).getName() + ((duel.getFirstTeam().size() > 1) ? "'s Team" : "");
                                this.plugin.getServer().broadcastMessage((e.getTeamNumber() == 1) ? winningTeamOne : winningTeamTwo);
                                match.setWinndingId(e.getTeamNumber());
                                match.setMatchState(TournamentMatch.MatchState.ENDING);
                                tournament.getTeams().remove((e.getTeamNumber() == 1) ? match.getSecondTeam() : match.getFirstTeam());
                                tournament.getCurrentQueue().remove((e.getTeamNumber() == 1) ? match.getSecondTeam() : match.getFirstTeam());
                                iterator.remove();
                            }
                        }
                    }
                }
            }
            String winnerElo = "";
            String loserElo = "";
            if (duel.isRanked()) {
                if (duel.getFirstTeam().size() == 1 && duel.getSecondTeam().size() == 1) {
                    final PracticePlayer winnerPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(winningTeam.get(0));
                    final PracticePlayer loserPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(losingTeam.get(0));
                    final int currentWinnerElo = winnerPracPlayer.getEloMap().get(duel.getKitName());
                    final int currentLoserElo = loserPracPlayer.getEloMap().get(duel.getKitName());
                    final int[] newElos = UtilElo.getNewRankings(currentWinnerElo, currentLoserElo, true);
                    winnerElo = ChatColor.GREEN + " +" + (newElos[0] - currentWinnerElo) + " (" + newElos[0] + ")";
                    loserElo = ChatColor.RED + " -" + (currentLoserElo - newElos[1]) + " (" + newElos[1] + ")";
                    winnerPracPlayer.addElo(duel.getKitName(), newElos[0]);
                    loserPracPlayer.addElo(duel.getKitName(), newElos[1]);
                    winnerPracPlayer.setRankedWins(winnerPracPlayer.getRankedWins() + 1);
                    loserPracPlayer.setRankedLosses(loserPracPlayer.getRankedLosses() + 1);
                }
                else if (duel.getFirstTeam().size() == 2 && duel.getSecondTeam().size() == 2 && duel.getFirstTeamPartyLeaderUUID() != null && duel.getSecondTeamPartyLeaderUUID() != null) {
                    final PracticePlayer winnerLeaderPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(winningTeam.get(0));
                    final PracticePlayer winnerMemberPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(winningTeam.get(1));
                    final PracticePlayer loserLeaderPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(losingTeam.get(0));
                    final PracticePlayer loserMemberPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(losingTeam.get(1));
                    final int currentWinnerElo2 = winnerLeaderPracPlayer.getPartyEloMap().get(winnerMemberPracPlayer.getUUID()).get(duel.getKitName());
                    final int currentLoserElo2 = loserLeaderPracPlayer.getPartyEloMap().get(loserMemberPracPlayer.getUUID()).get(duel.getKitName());
                    final int[] newElos2 = UtilElo.getNewRankings(currentWinnerElo2, currentLoserElo2, true);
                    winnerElo = ChatColor.GREEN + " +" + (newElos2[0] - currentWinnerElo2) + " (" + newElos2[0] + ")";
                    loserElo = ChatColor.RED + " -" + (currentLoserElo2 - newElos2[1]) + " (" + newElos2[1] + ")";
                    winnerLeaderPracPlayer.addPartyElo(winnerMemberPracPlayer.getUUID(), duel.getKitName(), newElos2[0]);
                    winnerMemberPracPlayer.addPartyElo(winnerLeaderPracPlayer.getUUID(), duel.getKitName(), newElos2[0]);
                    loserLeaderPracPlayer.addPartyElo(loserMemberPracPlayer.getUUID(), duel.getKitName(), newElos2[1]);
                    loserMemberPracPlayer.addPartyElo(loserLeaderPracPlayer.getUUID(), duel.getKitName(), newElos2[1]);
                }
            }
            if (duel.isPremium() && duel.getFirstTeam().size() == 1 && duel.getSecondTeam().size() == 1) {
                final PracticePlayer winnerPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(winningTeam.get(0));
                final PracticePlayer loserPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(losingTeam.get(0));
                final int currentWinnerElo = winnerPracPlayer.getPremiumEloMap().get(duel.getKitName());
                final int currentLoserElo = loserPracPlayer.getPremiumEloMap().get(duel.getKitName());
                final int[] newElos = UtilElo.getNewRankings(currentWinnerElo, currentLoserElo, true);
                winnerElo = ChatColor.GREEN + " +" + (newElos[0] - currentWinnerElo) + " (" + newElos[0] + ")";
                loserElo = ChatColor.RED + " -" + (currentLoserElo - newElos[1]) + " (" + newElos[1] + ")";
                winnerPracPlayer.addPremiumElo(duel.getKitName(), newElos[0]);
                loserPracPlayer.addPremiumElo(duel.getKitName(), newElos[1]);
                winnerPracPlayer.setPremiumWins(winnerPracPlayer.getPremiumWins() + 1);
                loserPracPlayer.setPremiumLosses(loserPracPlayer.getPremiumLosses() + 1);
            }
            boolean partyMatch = false;
            if (duel.getFirstTeam() != null && duel.getFirstTeam().size() > 1) {
                partyMatch = true;
            }
            for (final UUID uuidPlayer3 : winningTeam) {
                final Player player3 = this.plugin.getServer().getPlayer(uuidPlayer3);
                if (player3 == null) {
                    continue;
                }
                if (!duel.isRanked() && !duel.isPremium()) {
                    final PracticePlayer practicePlayer3 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player3.getUniqueId());
                    if (practicePlayer3 != null) {
                        practicePlayer3.setUnrankedWins(practicePlayer3.getUnrankedWins() + 1);
                    }
                }
                duelPlayers.add(player3);
                winnerMessage.addText(ChatColor.GREEN + (partyMatch ? "Winners: " : "Winner: ") + ChatColor.GRAY + player3.getName() + winnerElo + " ").addHoverText(ChatColor.GRAY + "Click here to view inventory").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(player3.getUniqueId()));
            }
            for (final UUID uuidPlayer3 : losingTeam) {
                final Player player3 = this.plugin.getServer().getPlayer(uuidPlayer3);
                if (player3 == null) {
                    continue;
                }
                if (!duel.isRanked() && !duel.isPremium()) {
                    final PracticePlayer practicePlayer3 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player3.getUniqueId());
                    if (practicePlayer3 != null) {
                        practicePlayer3.setUnrankedLosses(practicePlayer3.getUnrankedLosses() + 1);
                    }
                }
                duelPlayers.add(player3);
                loserMessage.addText(ChatColor.RED + (partyMatch ? "Losers: " : "Loser: ") + ChatColor.GRAY + player3.getName() + loserElo + " ").addHoverText(ChatColor.GRAY + "Click here to view inventory").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(player3.getUniqueId()));
            }
        }
        for (final UUID spectatorUUID : duel.getSpectators()) {
            final Player player4 = this.plugin.getServer().getPlayer(spectatorUUID);
            if (player4 == null) {
                continue;
            }
            duelPlayers.add(player4);
        }
        for (final Player player5 : duelPlayers) {
            final String[] information = { ChatColor.GOLD + "Match Results: " + ChatColor.GRAY + "(Clickable Inventories)" };
            player5.sendMessage(information);
            winnerMessage.sendToPlayer(player5);
            loserMessage.sendToPlayer(player5);
            Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), "knockback set default " + player5.getName());
        }
        for (final Player playerInDuel : duelPlayers) {
            final PlayerBoard playerBoard = this.plugin.getManagerHandler().getScoreboardHandler().getPlayerBoard(playerInDuel.getUniqueId());
            if (playerBoard != null) {}
        }
        duelPlayers.clear();
    }
    
    @EventHandler
    public void onDuelEnd(final DuelEndEvent e) {
        final Duel duel = e.getDuel();
        this.duelCountdownMap.remove(duel.getUUID());
        final BukkitTask task = this.duelTickThreadMap.get(duel.getUUID());
        task.cancel();
        this.duelTickThreadMap.remove(duel.getUUID());
        if (duel.getFfaPlayers() != null) {
            if (duel.getFfaPartyLeaderUUID() != null) {
                final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(duel.getFfaPartyLeaderUUID());
                if (party != null) {
                    party.setPartyState(PartyState.LOBBY);
                }
            }
            final Player player = this.plugin.getServer().getPlayer((UUID)duel.getFfaPlayersAlive().get(0));
            if (player == null) {
                return;
            }
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
        }
        else {
            if (duel.getFirstTeamPartyLeaderUUID() != null) {
                final Party firstTeamParty = this.plugin.getManagerHandler().getPartyManager().getParty(duel.getFirstTeamPartyLeaderUUID());
                if (firstTeamParty != null) {
                    firstTeamParty.setPartyState(PartyState.LOBBY);
                }
            }
            if (duel.getSecondTeamPartyLeaderUUID() != null) {
                final Party secondTeamParty = this.plugin.getManagerHandler().getPartyManager().getParty(duel.getSecondTeamPartyLeaderUUID());
                if (secondTeamParty != null) {
                    secondTeamParty.setPartyState(PartyState.LOBBY);
                }
            }
            for (final UUID uuid : duel.getFirstTeamAlive()) {
                final Player player2 = this.plugin.getServer().getPlayer(uuid);
                if (player2 == null) {
                    continue;
                }
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player2);
            }
            for (final UUID uuid : duel.getSecondTeamAlive()) {
                final Player player2 = this.plugin.getServer().getPlayer(uuid);
                if (player2 == null) {
                    continue;
                }
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player2);
            }
            if (duel.getSpectators().size() > 0) {
                final Iterator<UUID> iterator = duel.getSpectators().iterator();
                while (iterator.hasNext()) {
                    final UUID value = iterator.next();
                    final Player player2 = this.plugin.getServer().getPlayer(value);
                    if (player2 == null) {
                        continue;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player2);
                    this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player2, false);
                    iterator.remove();
                }
            }
        }
        this.plugin.getManagerHandler().getDuelManager().destroyDuel(duel);
    }
}
