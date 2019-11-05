package io.dhoom.runnables;

import org.bukkit.entity.*;
import io.dhoom.player.*;
import io.dhoom.*;

public class RemovePlayerTask implements Runnable
{
    private Player player;
    
    public RemovePlayerTask(final Player player) {
        this.player = player;
    }
    
    @Override
    public void run() {
        final PracticePlayer profile = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (profile != null) {
            profile.setGlobalPersonalElo(Practice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, true));
            profile.setGlobalPremiumElo(Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPremiumElo(profile));
            profile.setGlobalPartyElo(Practice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, false));
            profile.save();
            PracticePlayer.getProfiles().remove(profile);
        }
    }
}
