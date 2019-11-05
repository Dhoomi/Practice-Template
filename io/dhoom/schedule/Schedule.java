package io.dhoom.schedule;

import io.dhoom.*;
import org.bukkit.*;
import org.bson.*;
import com.mongodb.client.model.*;
import io.dhoom.player.*;
import com.mongodb.client.*;
import java.util.*;

public class Schedule
{
    private long eventTime;
    private long announceTime;
    
    public Schedule(final long eventTime) {
        this.eventTime = eventTime;
        this.announceTime = this.eventTime - 1800000L;
    }
    
    public void runEvent() {
        if (System.currentTimeMillis() > this.eventTime) {
            try {
                this.clearPremiumMatches();
                this.setNextEventTime();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (System.currentTimeMillis() > this.announceTime) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Practice.getInstance().getConfig().getString("listener.premium.reset")));
            this.setNextAnnounceTime();
        }
    }
    
    public long getEventTime() {
        return this.eventTime;
    }
    
    private void setNextEventTime() {
        this.eventTime += 604800000L;
    }
    
    private void setNextAnnounceTime() {
        this.announceTime += 604800000L;
    }
    
    private void clearPremiumMatches() {
        final FindIterable<Document> iterable = (FindIterable<Document>)Practice.getInstance().getPracticeDatabase().getProfiles().find();
        for (final Document document : iterable) {
            final UUID uuid = UUID.fromString(document.getString((Object)"uuid"));
            final int rankTokens = document.getInteger((Object)"rankPremiumTokens");
            document.put("premiumTokens", (Object)rankTokens);
            Practice.getInstance().getPracticeDatabase().getProfiles().replaceOne(Filters.eq("uuid", (Object)uuid.toString()), (Object)document, new UpdateOptions().upsert(true));
        }
        for (final PracticePlayer practicePlayer : PracticePlayer.getProfiles()) {
            practicePlayer.load();
        }
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Practice.getInstance().getConfig().getString("listener.premium.hasreset")));
    }
}
