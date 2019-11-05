package io.dhoom.kit;

import org.bukkit.inventory.*;
import java.util.*;

public class Kit
{
    private boolean enabled;
    private String name;
    private ItemStack icon;
    private boolean combo;
    private boolean editable;
    private boolean ranked;
    private boolean premium;
    private boolean builduhc;
    private ItemStack[] mainContents;
    private ItemStack[] armorContents;
    private ItemStack[] editorContents;
    
    public Kit(final String name, final ItemStack icon, final boolean combo, final boolean editable, final boolean ranked, final ItemStack[] mainContents, final ItemStack[] armorContents, final boolean premium, final boolean build) {
        this.name = name;
        this.icon = icon;
        this.combo = combo;
        this.editable = editable;
        this.ranked = ranked;
        this.premium = premium;
        this.mainContents = mainContents;
        this.armorContents = armorContents;
        this.builduhc = build;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ItemStack getIcon() {
        return this.icon;
    }
    
    public void setIcon(final ItemStack icon) {
        this.icon = icon;
    }
    
    public boolean isCombo() {
        return this.combo;
    }
    
    public void setCombo(final boolean combo) {
        this.combo = combo;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }
    
    public boolean isRanked() {
        return this.ranked;
    }
    
    public void setRanked(final boolean ranked) {
        this.ranked = ranked;
    }
    
    public boolean isPremium() {
        return this.premium;
    }
    
    public void setPremium(final boolean premium) {
        this.premium = premium;
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
    
    public List<ItemStack> getEditableContents() {
        final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (final ItemStack item : this.getMainContents()) {
            if (!items.contains(item)) {
                items.add(item);
            }
        }
        return items;
    }
    
    public boolean isBuilduhc() {
        return this.builduhc;
    }
    
    public void setBuilduhc(final boolean builduhc) {
        this.builduhc = builduhc;
    }
}
