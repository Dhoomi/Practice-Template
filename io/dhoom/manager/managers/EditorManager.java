package io.dhoom.manager.managers;

import io.dhoom.player.*;
import io.dhoom.manager.*;
import java.util.*;
import io.dhoom.kit.*;

public class EditorManager extends Manager
{
    private Map<UUID, String> editingKit;
    private Map<UUID, PlayerKit> renamingKit;
    
    public EditorManager(final ManagerHandler handler) {
        super(handler);
        this.editingKit = new HashMap<UUID, String>();
        this.renamingKit = new HashMap<UUID, PlayerKit>();
    }
    
    public String getPlayerEditingKit(final UUID uuid) {
        return this.editingKit.get(uuid);
    }
    
    public PlayerKit getKitRenaming(final UUID uuid) {
        return this.renamingKit.get(uuid);
    }
    
    public void addEditingKit(final UUID uuid, final Kit kit) {
        this.editingKit.put(uuid, kit.getName());
        this.handler.getInventoryManager().addEditKitItemsInventory(uuid, kit);
        this.handler.getInventoryManager().addEditKitKitsInventory(uuid, kit);
    }
    
    public void addRenamingKit(final UUID uuid, final PlayerKit playerKit) {
        this.renamingKit.put(uuid, playerKit);
    }
    
    public void removeEditingKit(final UUID uuid) {
        this.editingKit.remove(uuid);
        this.renamingKit.remove(uuid);
        this.handler.getInventoryManager().destroyEditKitItemsInventory(uuid);
        this.handler.getInventoryManager().destroyEditKitKitsInventory(uuid);
    }
    
    public void removeRenamingKit(final UUID uuid) {
        this.renamingKit.remove(uuid);
    }
    
    public Map<UUID, PlayerKit> getRenamingKit() {
        return this.renamingKit;
    }
}
