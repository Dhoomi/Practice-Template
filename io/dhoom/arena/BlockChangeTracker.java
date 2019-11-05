package io.dhoom.arena;

import org.bukkit.block.*;
import java.util.*;
import org.bukkit.*;
import com.google.common.collect.*;

public class BlockChangeTracker
{
    private LinkedList<BlockState> changeTrackers;
    private Set<Location> playerPlacedBlocks;
    
    public BlockChangeTracker() {
        this.changeTrackers = (LinkedList<BlockState>)Lists.newLinkedList();
        this.playerPlacedBlocks = (Set<Location>)Sets.newHashSet();
    }
    
    public synchronized void rollback() {
        BlockState blockState;
        while ((blockState = this.changeTrackers.pollLast()) != null) {
            blockState.update(true, false);
        }
        this.playerPlacedBlocks.clear();
    }
    
    public void setPlayerPlacedBlock(final Location location) {
        this.playerPlacedBlocks.add(location);
    }
    
    public boolean isPlayerPlacedBlock(final Location location) {
        return this.playerPlacedBlocks.contains(location);
    }
    
    public void add(final BlockState blockState) {
        this.changeTrackers.add(blockState);
    }
}
