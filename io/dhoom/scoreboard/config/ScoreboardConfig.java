package io.dhoom.scoreboard.config;

import java.util.*;
import org.bukkit.configuration.file.*;
import org.bukkit.*;
import com.google.common.base.*;
import javax.annotation.*;
import com.google.common.collect.*;

public class ScoreboardConfig
{
    private Map<ScoreboardType, List<String>> scoreboards;
    
    public ScoreboardConfig(final FileConfiguration configuration) {
        this.scoreboards = (Map<ScoreboardType, List<String>>)Maps.newHashMap();
        for (final ScoreboardType scoreboardType : ScoreboardType.values()) {
            this.scoreboards.put(scoreboardType, translate(configuration.getStringList("SCOREBOARD." + scoreboardType.name())));
        }
    }
    
    public static String translate(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
    
    public static List<String> translate(final List<String> input) {
        return (List<String>)Lists.newArrayList((Iterable)Lists.transform((List)input, (Function)new Function<String, String>() {
            public String apply(@Nullable final String s) {
                return ScoreboardConfig.translate(s);
            }
        }));
    }
    
    public List<String> get(final ScoreboardType scoreboardType) {
        return this.scoreboards.get(scoreboardType);
    }
}
