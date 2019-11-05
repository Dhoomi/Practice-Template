package io.dhoom.runnables.other;

import io.kipes.*;

public class UpdateInventoryTask implements Runnable
{
    private Practice plugin;
    private InventoryTaskType inventoryTaskType;
    
    public UpdateInventoryTask(final Practice plugin, final InventoryTaskType inventoryTaskType) {
        this.plugin = plugin;
        this.inventoryTaskType = inventoryTaskType;
    }
    
    @Override
    public void run() {
        if (this.inventoryTaskType == InventoryTaskType.UNRANKED_SOLO) {
            this.plugin.getManagerHandler().getInventoryManager().setUnrankedInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.UNRANKED_PARTY) {
            this.plugin.getManagerHandler().getInventoryManager().setUnrankedPartyInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.RANKED_SOLO) {
            this.plugin.getManagerHandler().getInventoryManager().setRankedInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.RANKED_PARTY) {
            this.plugin.getManagerHandler().getInventoryManager().setRankedPartyInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.PREMIUM_SOLO) {
            this.plugin.getManagerHandler().getInventoryManager().setPremiumInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.SPECTATOR) {
            this.plugin.getManagerHandler().getInventoryManager().setSpectatorInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.TOURNAMENT) {
            this.plugin.getManagerHandler().getInventoryManager().setJoinTournamentInventory();
        }
    }
    
    public enum InventoryTaskType
    {
        UNRANKED_SOLO, 
        RANKED_SOLO, 
        RANKED_PARTY, 
        UNRANKED_PARTY, 
        PREMIUM_SOLO, 
        SPECTATOR, 
        TOURNAMENT;
    }
}
