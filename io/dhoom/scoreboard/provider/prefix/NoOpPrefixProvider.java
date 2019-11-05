package io.dhoom.scoreboard.provider.prefix;

import io.kipes.scoreboard.provider.*;
import org.bukkit.entity.*;

public class NoOpPrefixProvider implements PrefixProvider
{
    @Override
    public String getPrefix(final Player player) {
        return "";
    }
}
