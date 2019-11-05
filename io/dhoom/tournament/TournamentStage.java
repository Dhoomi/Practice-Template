package io.dhoom.tournament;

import org.apache.commons.lang.*;
import com.google.common.base.*;

public enum TournamentStage
{
    FIRST_ROUND, 
    SECOND_ROUND, 
    THIRD_ROUND, 
    QUARTER_FINALS, 
    SEMI_FINALS, 
    FINALS;
    
    public String toReadable() {
        final String[] split = this.name().split("_");
        for (int i = 0; i < split.length; ++i) {
            split[i] = WordUtils.capitalize(split[i].toLowerCase());
        }
        return Joiner.on(" ").join((Object[])split);
    }
}
