package com.helion3.keys.listeners;

import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ExplosionEvent;

import com.helion3.keys.Keys;

public class ExplosionListener {
    @Listener
    public void onExplosion(final ExplosionEvent.Detonate event) {
        Optional<Entity> optional = event.getCause().first(Entity.class);
        if (optional.isPresent()) {
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                if (Keys.getLockableBlocks().contains(transaction.getFinal().getState().getType())) {
                    try {
                        // Are there locks on this block?
                        if (!Keys.getStorageAdapter().getLocks(transaction.getOriginal().getLocation().get()).isEmpty()) {
                            transaction.setValid(false);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}