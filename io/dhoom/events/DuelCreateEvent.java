package io.dhoom.events;

import org.bukkit.event.*;
import io.dhoom.duel.*;

public class DuelCreateEvent extends Event
{
    private static HandlerList handlerList;
    private Duel duel;
    
    public DuelCreateEvent(final Duel duel) {
        this.duel = duel;
    }
    
    public static HandlerList getHandlerList() {
        return DuelCreateEvent.handlerList;
    }
    
    public Duel getDuel() {
        return this.duel;
    }
    
    public HandlerList getHandlers() {
        return DuelCreateEvent.handlerList;
    }
    
    static {
        DuelCreateEvent.handlerList = new HandlerList();
    }
}
