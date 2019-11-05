package io.dhoom.player;

import org.bukkit.inventory.*;

public class PlayerKit
{
    private String kitName;
    private int kitIndex;
    private String displayName;
    private ItemStack[] mainContents;
    private ItemStack[] armorContents;
    
    public PlayerKit(final String kitName, final int kitIndex, final String displayName, final ItemStack[] mainContents, final ItemStack[] armorContents) {
        this.kitName = kitName;
        this.kitIndex = kitIndex;
        this.displayName = displayName;
        this.mainContents = mainContents;
        this.armorContents = armorContents;
    }
    
    public String getKitName() {
        return this.kitName;
    }
    
    public int getKitIndex() {
        return this.kitIndex;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public ItemStack[] getMainContents() {
        return this.mainContents;
    }
    
    public void setMainContents(final ItemStack[] mainContents) {
        this.mainContents = mainContents;
    }
    
    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }
    
    public void setArmorContents(final ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }
}
