package io.dhoom.lang;

import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.*;
import com.google.common.io.*;
import java.nio.charset.*;
import java.io.*;
import org.bukkit.configuration.*;

public class ConfigurationWrapper
{
    private File file;
    private String name;
    private YamlConfiguration config;
    private JavaPlugin plugin;
    
    public ConfigurationWrapper(final String name, final JavaPlugin plugin) {
        this.plugin = plugin;
        this.name = name;
        plugin.getDataFolder().mkdir();
        this.file = new File(plugin.getDataFolder(), name);
        this.saveDefault();
        this.reloadConfig();
    }
    
    public void saveDefault() {
        if (!this.file.exists()) {
            final InputStream defConfigStream = this.plugin.getResource(this.name);
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(this.file);
                ByteStreams.copy(defConfigStream, (OutputStream)stream);
            }
            catch (IOException var12) {
                var12.printStackTrace();
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException var13) {
                        var13.printStackTrace();
                    }
                }
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException var14) {
                        var14.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
        final InputStream defConfigStream = this.plugin.getResource(this.name);
        if (defConfigStream != null) {
            final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration((Reader)new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));
            this.config.setDefaults((Configuration)defConfig);
        }
        try {
            this.config.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public File getFile() {
        return this.file;
    }
    
    public YamlConfiguration getConfig() {
        return this.config;
    }
}
