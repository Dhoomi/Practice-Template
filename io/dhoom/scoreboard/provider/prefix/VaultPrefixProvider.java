package io.dhoom.scoreboard.provider.prefix;

import io.kipes.scoreboard.provider.*;
import net.milkbowl.vault.chat.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.plugin.*;

public class VaultPrefixProvider implements PrefixProvider
{
    private static Chat vaultChat;
    
    public static String color(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    @Override
    public String getPrefix(final Player paramPlayer) {
        if (VaultPrefixProvider.vaultChat == null) {
            final RegisteredServiceProvider<Chat> provider = (RegisteredServiceProvider<Chat>)Bukkit.getServicesManager().getRegistration((Class)Chat.class);
            if (provider != null) {
                VaultPrefixProvider.vaultChat = (Chat)provider.getProvider();
            }
        }
        final String prefix = (VaultPrefixProvider.vaultChat == null) ? "" : VaultPrefixProvider.vaultChat.getPlayerPrefix(paramPlayer);
        return color(prefix);
    }
}
