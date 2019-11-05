package io.dhoom.player;

import io.kipes.*;
import io.kipes.settings.*;
import com.google.common.collect.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bson.*;
import org.json.simple.*;
import java.net.*;
import java.io.*;
import org.json.simple.parser.*;
import com.google.gson.*;
import com.mongodb.client.model.*;
import java.util.*;

public class PracticePlayer
{
    public static Practice main;
    private static Set<PracticePlayer> profiles;
    private UUID uuid;
    private String name;
    private PlayerState currentState;
    private int teamNumber;
    private int rankPremiumTokens;
    private int premiumTokens;
    private int premiumWins;
    private int premiumLosses;
    private int rankedWins;
    private int rankedLosses;
    private int unrankedWins;
    private int unrankedLosses;
    private int globalPersonalElo;
    private int globalPartyElo;
    private int globalPremiumElo;
    private int credits;
    private boolean scoreboard;
    private long hostCooldown;
    private List<Match> matches;
    private Map<String, Map<Integer, PlayerKit>> playerKitMap;
    private Map<String, Integer> playerEloMap;
    private Map<String, Integer> premiumEloMap;
    private Map<UUID, Map<String, Integer>> partyEloMap;
    private Settings settings;
    private transient LinkedList<Projectile> projectileQueue;
    private transient boolean showRematchItemFlag;
    private transient String lastDuelPlayer;
    
    public PracticePlayer(final UUID uuid, final boolean cache) {
        this.matches = new ArrayList<Match>();
        this.playerKitMap = new HashMap<String, Map<Integer, PlayerKit>>();
        this.playerEloMap = new HashMap<String, Integer>();
        this.premiumEloMap = new HashMap<String, Integer>();
        this.partyEloMap = new HashMap<UUID, Map<String, Integer>>();
        this.settings = new Settings();
        this.projectileQueue = (LinkedList<Projectile>)Lists.newLinkedList();
        this.uuid = uuid;
        this.currentState = PlayerState.LOBBY;
        this.premiumTokens = 0;
        this.rankPremiumTokens = 0;
        this.rankedWins = 0;
        this.unrankedWins = 0;
        this.premiumWins = 0;
        this.rankedLosses = 0;
        this.unrankedLosses = 0;
        this.premiumLosses = 0;
        this.credits = 0;
        this.globalPartyElo = 0;
        this.globalPersonalElo = 0;
        this.globalPremiumElo = 0;
        this.hostCooldown = 0L;
        this.scoreboard = true;
        final Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.name = player.getName();
        }
        else {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer != null) {
                this.name = offlinePlayer.getName();
            }
        }
        this.load();
        if (cache) {
            PracticePlayer.profiles.add(this);
        }
    }
    
    public static PracticePlayer getByUuid(final UUID uuid) {
        for (final PracticePlayer profile : PracticePlayer.profiles) {
            if (!profile.getUUID().equals(uuid)) {
                continue;
            }
            return profile;
        }
        return getExternalByUuid(uuid);
    }
    
    private static PracticePlayer getExternalByUuid(final UUID uuid) {
        final PracticePlayer profile = new PracticePlayer(uuid, false);
        return profile;
    }
    
    public static Map.Entry<UUID, String> getExternalPlayerInformation(String name) throws IOException, ParseException {
        final Document document = (Document)PracticePlayer.main.getPracticeDatabase().getProfiles().find(Filters.eq("recentName", (Object)name)).first();
        if (document != null && document.containsKey((Object)"recentName")) {
            return new AbstractMap.SimpleEntry<UUID, String>(UUID.fromString(document.getString((Object)"uuid")), document.getString((Object)"recentName"));
        }
        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        final URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final JSONParser parser = new JSONParser();
        final JSONObject obj = (JSONObject)parser.parse(reader.readLine());
        final UUID uuid = UUID.fromString(String.valueOf(obj.get((Object)"id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        name = String.valueOf(obj.get((Object)"name"));
        reader.close();
        return new AbstractMap.SimpleEntry<UUID, String>(uuid, name);
    }
    
    public static Set<PracticePlayer> getProfiles() {
        return PracticePlayer.profiles;
    }
    
    public int getCredits() {
        return this.credits;
    }
    
    public void setCredits(final int credits) {
        this.credits = credits;
    }
    
    public long getHostCooldown() {
        return this.hostCooldown;
    }
    
    public void setHostCooldown(final long hostCooldown) {
        this.hostCooldown = hostCooldown;
    }
    
    public boolean isScoreboard() {
        return this.scoreboard;
    }
    
    public void setScoreboard(final boolean scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public int getTeamNumber() {
        return this.teamNumber;
    }
    
    public void setTeamNumber(final int i) {
        this.teamNumber = i;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public PlayerState getCurrentState() {
        return this.currentState;
    }
    
    public void setCurrentState(final PlayerState playerState) {
        this.currentState = playerState;
    }
    
    public void addElo(final String kitName, final int elo) {
        this.playerEloMap.put(kitName, elo);
    }
    
    public void addPremiumElo(final String kitName, final int elo) {
        this.premiumEloMap.put(kitName, elo);
    }
    
    public void addPartyElo(final UUID uuid, final String kitName, final int elo) {
        if (!this.partyEloMap.containsKey(uuid)) {
            this.partyEloMap.put(uuid, new HashMap<String, Integer>());
        }
        this.partyEloMap.get(uuid).put(kitName, elo);
    }
    
    public void addKit(final String kitName, final Integer kitIndex, final PlayerKit playerKit) {
        if (!this.playerKitMap.containsKey(kitName)) {
            this.playerKitMap.put(kitName, new HashMap<Integer, PlayerKit>());
        }
        this.playerKitMap.get(kitName).put(kitIndex, playerKit);
    }
    
    public Map<String, Integer> getEloMap() {
        return this.playerEloMap;
    }
    
    public List<Match> getMatches() {
        return this.matches;
    }
    
    public Map<String, Integer> getPremiumEloMap() {
        return this.premiumEloMap;
    }
    
    public Map<String, Map<Integer, PlayerKit>> getKitMap() {
        return this.playerKitMap;
    }
    
    public Map<UUID, Map<String, Integer>> getPartyEloMap() {
        return this.partyEloMap;
    }
    
    public void load() {
        final Document document = (Document)PracticePlayer.main.getPracticeDatabase().getProfiles().find(Filters.eq("uuid", (Object)this.uuid.toString())).first();
        if (document != null) {
            for (final JsonElement element : new JsonParser().parse(document.getString((Object)"player_elo")).getAsJsonArray()) {
                final JsonObject practiceDocument = element.getAsJsonObject();
                if (practiceDocument.has("kit_personal_name")) {
                    this.addElo(practiceDocument.get("kit_personal_name").getAsString(), practiceDocument.get("kit_personal_elo").getAsInt());
                }
                if (practiceDocument.has("kit_premium_name")) {
                    this.addPremiumElo(practiceDocument.get("kit_premium_name").getAsString(), practiceDocument.get("kit_premium_elo").getAsInt());
                }
                if (!practiceDocument.has("kit_party_uuid")) {
                    continue;
                }
                this.addPartyElo(UUID.fromString(practiceDocument.get("kit_party_uuid").getAsString()), practiceDocument.get("kit_party_name").getAsString(), practiceDocument.get("kit_party_elo").getAsInt());
            }
            if (document.containsKey((Object)"rankedWins")) {
                this.rankedWins = document.getInteger((Object)"rankedWins");
            }
            if (document.containsKey((Object)"rankedLosses")) {
                this.rankedLosses = document.getInteger((Object)"rankedLosses");
            }
            if (document.containsKey((Object)"unrankedWins")) {
                this.unrankedWins = document.getInteger((Object)"unrankedWins");
            }
            if (document.containsKey((Object)"unrankedLosses")) {
                this.unrankedLosses = document.getInteger((Object)"unrankedLosses");
            }
            if (document.containsKey((Object)"premiumWins")) {
                this.premiumWins = document.getInteger((Object)"premiumWins");
            }
            if (document.containsKey((Object)"premiumLosses")) {
                this.premiumLosses = document.getInteger((Object)"premiumLosses");
            }
            if (document.containsKey((Object)"hostCooldown")) {
                this.hostCooldown = document.getLong((Object)"hostCooldown");
            }
            if (document.containsKey((Object)"globalPersonalElo")) {
                this.globalPersonalElo = document.getInteger((Object)"globalPersonalElo");
            }
            if (document.containsKey((Object)"globalPartyElo")) {
                this.globalPartyElo = document.getInteger((Object)"globalPartyElo");
            }
            if (document.containsKey((Object)"globalPremiumElo")) {
                this.globalPremiumElo = document.getInteger((Object)"globalPremiumElo");
            }
            if (document.containsKey((Object)"premiumTokens")) {
                this.premiumTokens = document.getInteger((Object)"premiumTokens");
            }
            if (document.containsKey((Object)"rankPremiumTokens")) {
                this.rankPremiumTokens = document.getInteger((Object)"rankPremiumTokens");
            }
            if (document.containsKey((Object)"credits")) {
                this.credits = document.getInteger((Object)"credits");
            }
            if (document.containsKey((Object)"scoreboard")) {
                this.scoreboard = document.getBoolean((Object)"scoreboard");
            }
            if (document.containsKey((Object)"recentName")) {
                this.name = document.getString((Object)"recentName");
            }
        }
    }
    
    public void save() {
        final Document document = new Document();
        final JsonArray eloDocument = new JsonArray();
        final JsonArray matchesDocument = new JsonArray();
        for (final Map.Entry<String, Integer> entry : this.playerEloMap.entrySet()) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("kit_personal_name", (String)entry.getKey());
            practiceDocument.addProperty("kit_personal_elo", (Number)entry.getValue());
            eloDocument.add((JsonElement)practiceDocument);
        }
        for (final Map.Entry<String, Integer> entry : this.premiumEloMap.entrySet()) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("kit_premium_name", (String)entry.getKey());
            practiceDocument.addProperty("kit_premium_elo", (Number)entry.getValue());
            eloDocument.add((JsonElement)practiceDocument);
        }
        for (final Map.Entry<UUID, Map<String, Integer>> entry2 : this.partyEloMap.entrySet()) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("kit_party_uuid", entry2.getKey().toString());
            for (final Map.Entry<String, Integer> sideEntry : entry2.getValue().entrySet()) {
                practiceDocument.addProperty("kit_party_name", (String)sideEntry.getKey());
                practiceDocument.addProperty("kit_party_elo", (Number)sideEntry.getValue());
            }
            eloDocument.add((JsonElement)practiceDocument);
        }
        for (final Match match : this.matches) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("matchId", match.getMatchId().toString());
            practiceDocument.addProperty("items", match.getItems());
            practiceDocument.addProperty("armor", match.getArmor());
            practiceDocument.addProperty("potions", match.getPotions());
            practiceDocument.addProperty("eloChangeLoser", (Number)match.getEloChangeLoser());
            practiceDocument.addProperty("eloChangeWinner", (Number)match.getEloChangeWinner());
            practiceDocument.addProperty("winningTeamId", (Number)match.getWinningTeamId());
            practiceDocument.addProperty("firstTeam", match.getFirstTeam());
            practiceDocument.addProperty("secondTeam", match.getSecondTeam());
            matchesDocument.add((JsonElement)practiceDocument);
        }
        document.put("uuid", (Object)this.uuid.toString());
        document.put("credits", (Object)this.credits);
        document.put("scoreboard", (Object)this.scoreboard);
        document.put("premiumTokens", (Object)this.premiumTokens);
        document.put("rankPremiumTokens", (Object)this.rankPremiumTokens);
        document.put("rankedWins", (Object)this.rankedWins);
        document.put("rankedLosses", (Object)this.rankedLosses);
        document.put("unrankedWins", (Object)this.unrankedWins);
        document.put("unrankedLosses", (Object)this.unrankedLosses);
        document.put("premiumWins", (Object)this.premiumWins);
        document.put("premiumLosses", (Object)this.premiumLosses);
        document.put("globalPersonalElo", (Object)this.globalPersonalElo);
        document.put("globalPartyElo", (Object)this.globalPartyElo);
        document.put("globalPremiumElo", (Object)this.globalPremiumElo);
        document.put("hostCooldown", (Object)this.hostCooldown);
        if (this.name != null) {
            document.put("recentName", (Object)this.name);
            document.put("recentNameLowercase", (Object)this.name.toLowerCase());
            document.put("recentNameLength", (Object)this.name.length());
        }
        document.put("player_elo", (Object)eloDocument.toString());
        document.put("matches", (Object)matchesDocument.toString());
        PracticePlayer.main.getPracticeDatabase().getProfiles().replaceOne(Filters.eq("uuid", (Object)this.uuid.toString()), (Object)document, new UpdateOptions().upsert(true));
    }
    
    public int getRankPremiumTokens() {
        return this.rankPremiumTokens;
    }
    
    public void setRankPremiumTokens(final int rankPremiumTokens) {
        this.rankPremiumTokens = rankPremiumTokens;
    }
    
    public int getPremiumTokens() {
        return this.premiumTokens;
    }
    
    public void setPremiumTokens(final int premiumTokens) {
        this.premiumTokens = premiumTokens;
    }
    
    public int getPremiumWins() {
        return this.premiumWins;
    }
    
    public void setPremiumWins(final int premiumWins) {
        this.premiumWins = premiumWins;
    }
    
    public int getPremiumLosses() {
        return this.premiumLosses;
    }
    
    public void setPremiumLosses(final int premiumLosses) {
        this.premiumLosses = premiumLosses;
    }
    
    public int getRankedWins() {
        return this.rankedWins;
    }
    
    public void setRankedWins(final int rankedWins) {
        this.rankedWins = rankedWins;
    }
    
    public int getRankedLosses() {
        return this.rankedLosses;
    }
    
    public void setRankedLosses(final int rankedLosses) {
        this.rankedLosses = rankedLosses;
    }
    
    public int getUnrankedWins() {
        return this.unrankedWins;
    }
    
    public void setUnrankedWins(final int unrankedWins) {
        this.unrankedWins = unrankedWins;
    }
    
    public int getUnrankedLosses() {
        return this.unrankedLosses;
    }
    
    public void setUnrankedLosses(final int unrankedLosses) {
        this.unrankedLosses = unrankedLosses;
    }
    
    public int getGlobalPersonalElo() {
        return this.globalPersonalElo;
    }
    
    public void setGlobalPersonalElo(final int globalPersonalElo) {
        this.globalPersonalElo = globalPersonalElo;
    }
    
    public int getGlobalPartyElo() {
        return this.globalPartyElo;
    }
    
    public void setGlobalPartyElo(final int globalPartyElo) {
        this.globalPartyElo = globalPartyElo;
    }
    
    public int getGlobalPremiumElo() {
        return this.globalPremiumElo;
    }
    
    public void setGlobalPremiumElo(final int globalPremiumElo) {
        this.globalPremiumElo = globalPremiumElo;
    }
    
    public Settings getSettings() {
        return this.settings;
    }
    
    public void setSettings(final Settings settings) {
        this.settings = settings;
    }
    
    public LinkedList<Projectile> getProjectileQueue() {
        return this.projectileQueue;
    }
    
    public boolean isShowRematchItemFlag() {
        return this.showRematchItemFlag;
    }
    
    public void setShowRematchItemFlag(final boolean showRematchItemFlag) {
        this.showRematchItemFlag = showRematchItemFlag;
    }
    
    public String getLastDuelPlayer() {
        return this.lastDuelPlayer;
    }
    
    public void setLastDuelPlayer(final String lastDuelPlayer) {
        this.lastDuelPlayer = lastDuelPlayer;
    }
    
    static {
        PracticePlayer.main = Practice.getInstance();
        PracticePlayer.profiles = new HashSet<PracticePlayer>();
    }
}
