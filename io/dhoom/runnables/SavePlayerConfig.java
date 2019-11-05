package io.dhoom.runnables;

import io.kipes.*;
import io.kipes.util.*;
import org.bukkit.plugin.java.*;
import io.kipes.kit.*;
import io.kipes.player.*;
import java.util.*;

public class SavePlayerConfig implements Runnable
{
    private UUID uuid;
    private Practice plugin;
    
    public SavePlayerConfig(final UUID uuid, final Practice plugin) {
        this.uuid = uuid;
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(this.uuid);
        if (practicePlayer == null) {
            return;
        }
        final Config config = new Config(this.plugin, "/players", this.uuid.toString());
        for (final Kit kit : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
            if (!practicePlayer.getKitMap().containsKey(kit.getName())) {
                continue;
            }
            final Map<Integer, PlayerKit> playerKits = practicePlayer.getKitMap().get(kit.getName());
            for (final PlayerKit playerKit : playerKits.values()) {
                config.getConfig().set("playerkits." + kit.getName() + "." + playerKit.getKitIndex() + ".displayName", (Object)playerKit.getDisplayName());
                config.getConfig().set("playerkits." + kit.getName() + "." + playerKit.getKitIndex() + ".mainContents", (Object)playerKit.getMainContents());
                config.getConfig().set("playerkits." + kit.getName() + "." + playerKit.getKitIndex() + ".armorContents", (Object)playerKit.getArmorContents());
            }
        }
        config.save();
    }
}
