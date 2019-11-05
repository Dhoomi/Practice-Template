package io.dhoom.settings;

public class Settings
{
    private boolean scoreboard;
    private boolean duelRequests;
    private boolean publicChat;
    private boolean toggletime;
    
    public Settings() {
        this.scoreboard = true;
        this.duelRequests = true;
        this.publicChat = true;
        this.toggletime = true;
    }
    
    public boolean isToggletime() {
        return this.toggletime;
    }
    
    public void setToggletime(final boolean toggletime) {
        this.toggletime = toggletime;
    }
    
    public boolean isScoreboard() {
        return this.scoreboard;
    }
    
    public void setScoreboard(final boolean scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public boolean isDuelRequests() {
        return this.duelRequests;
    }
    
    public void setDuelRequests(final boolean duelRequests) {
        this.duelRequests = duelRequests;
    }
    
    public boolean isPublicChat() {
        return this.publicChat;
    }
    
    public void setPublicChat(final boolean publicChat) {
        this.publicChat = publicChat;
    }
}
