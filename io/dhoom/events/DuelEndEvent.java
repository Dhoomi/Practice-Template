package io.dhoom.events;

import org.bukkit.event.*;
import io.dhoom.duel.*;

public class DuelEndEvent extends Event
{
    private static HandlerList handlerList;
    private Duel duel;
    
    public DuelEndEvent(final Duel duel) {
        this.duel = duel;
    }
    
    public static HandlerList getHandlerList() {
        return DuelEndEvent.handlerList;
    }
    
    public Duel getDuel() {
        return this.duel;
    }
    
    public HandlerList getHandlers() {
        return DuelEndEvent.handlerList;
    }
    
    static {
        DuelEndEvent.handlerList = new HandlerList();
    }
}
