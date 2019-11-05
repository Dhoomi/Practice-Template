package io.dhoom.lang;

import org.bukkit.configuration.file.*;
import com.google.common.base.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.command.*;

public class Lang
{
    private static Lang LANG;
    private Map<String, String> language;
    
    public Lang() {
        this.language = new HashMap<String, String>();
    }
    
    public static Lang getLang() {
        return Lang.LANG;
    }
    
    public void add(final FileConfiguration configuration) {
        for (final String keys : configuration.getKeys(false)) {
            if (configuration.isList(keys)) {
                this.language.put(keys, ChatColor.translateAlternateColorCodes('&', Joiner.on("\n").join((Iterable)configuration.getStringList(keys))));
            }
            else {
                if (!configuration.isString(keys)) {
                    continue;
                }
                this.language.put(keys, ChatColor.translateAlternateColorCodes('&', configuration.getString(keys)));
            }
        }
    }
    
    public String getLocalized(final CommandSender consoleSender, final String key) {
        final String localized = this.language.get(key);
        if (localized == null) {
            return "Missing <" + key + ">";
        }
        return localized;
    }
    
    static {
        Lang.LANG = new Lang();
    }
}
