package io.dhoom.util;

import java.io.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.*;
import org.bukkit.*;
import java.util.*;

public class ConfigFile
{
    private File file;
    private YamlConfiguration configuration;
    
    public ConfigFile(final JavaPlugin plugin, final String name) {
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdir();
        }
        plugin.saveResource(name + ".yml", false);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }
    
    public double getDouble(final String path) {
        return this.configuration.contains(path) ? this.configuration.getDouble(path) : 0.0;
    }
    
    public int getInt(final String path) {
        return this.configuration.contains(path) ? this.configuration.getInt(path) : 0;
    }
    
    public boolean getBoolean(final String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }
    
    public String getString(final String path) {
        return this.configuration.contains(path) ? ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : "ERROR: STRING NOT FOUND";
    }
    
    public String getString(final String path, final String callback, final boolean colorize) {
        return this.configuration.contains(path) ? (colorize ? ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : this.configuration.getString(path)) : callback;
    }
    
    public List<String> getReversedStringList(final String path) {
        final List<String> list = this.getStringList(path);
        if (list == null) {
            return Arrays.asList("ERROR: STRING LIST NOT FOUND!");
        }
        final int size = list.size();
        final ArrayList<String> toReturn = new ArrayList<String>();
        for (int i = size - 1; i >= 0; --i) {
            toReturn.add(list.get(i));
        }
        return toReturn;
    }
    
    public List<String> getStringList(final String path) {
        if (!this.configuration.contains(path)) {
            return Arrays.asList("ERROR: STRING LIST NOT FOUND!");
        }
        final ArrayList<String> strings = new ArrayList<String>();
        for (final String string : this.configuration.getStringList(path)) {
            strings.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return strings;
    }
    
    public List<String> getStringListOrDefault(final String path, final List<String> toReturn) {
        if (!this.configuration.contains(path)) {
            return toReturn;
        }
        final ArrayList<String> strings = new ArrayList<String>();
        for (final String string : this.configuration.getStringList(path)) {
            strings.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return strings;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }
}
