package io.dhoom.scoreboard.provider.prefix;

import io.kipes.scoreboard.provider.*;
import org.bukkit.entity.*;
import io.kipes.groups.profile.*;
import org.bukkit.*;
import io.kipes.groups.rank.*;

public class GroupProvider implements PrefixProvider
{
    @Override
    public String getPrefix(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile == null) {
            return "";
        }
        final Rank rank = profile.getActiveGrant().getRank();
        if (rank.getData().getPrefix().isEmpty()) {
            return "";
        }
        char code = 'f';
        for (final String string : rank.getData().getPrefix().split("&")) {
            if (!string.isEmpty() && ChatColor.getByChar(string.toCharArray()[0]) != null) {
                code = string.toCharArray()[0];
            }
        }
        final ChatColor color = ChatColor.getByChar(code);
        return color.toString();
    }
}
