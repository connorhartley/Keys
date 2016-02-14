/**
 * This file is part of Keys, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.keys.listeners;

import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.flowpowered.math.vector.Vector3i;
import com.helion3.keys.Keys;
import com.helion3.keys.util.Format;
import org.spongepowered.api.util.Direction;

public class ChangeBlockListener {
    @Listener
    public void onChangeBlock(final ChangeBlockEvent event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (!optionalPlayer.isPresent()) {
            return;
        }

        Player player = optionalPlayer.get();

        // Are they placing a locked item?
        if (event instanceof ChangeBlockEvent.Place && player.hasPermission("keys.use")) {
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                if (Keys.getAutoLockedBlocks().contains(transaction.getFinal().getState().getType())) {
                    // Skip "place" events that are really just "state change" events
                    if (transaction.getOriginal().getState().getType().equals(BlockTypes.LIT_FURNACE)) {
                        continue;
                    }

                    try {
                        Keys.getStorageAdapter().setLock(player, transaction.getFinal().getLocation().get());

                        // Build message
                        String blockName = transaction.getFinal().getState().getType().getName().replace("minecraft:", "").replace("_", " ");
                        Vector3i position = transaction.getFinal().getLocation().get().getPosition().toInt();
                        String message = String.format("Auto-locking %s at %d %d %d", blockName, position.getX(), position.getY(), position.getZ());

                        // Send message
                        player.sendMessage(Format.success(message));
                    } catch (SQLException e) {
                        player.sendMessage(Format.error("Storage error. Details have been logged."));
                        e.printStackTrace();
                    }
                }
            }
        }

        // Are they breaking a locked item?
        if (event instanceof ChangeBlockEvent.Break) {
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                // Block
                if (!handleBreak(player, transaction, transaction.getOriginal())) {
                    // Block above
                    handleBreak(player, transaction, transaction.getOriginal().getLocation().get().getRelative(Direction.UP).createSnapshot());
                }
            }
        }
    }

    private boolean handleBreak(Player player, Transaction<BlockSnapshot> transaction, BlockSnapshot block) {
        if (Keys.getLockableBlocks().contains(block.getState().getType())) {
            try {
                // Is user allowed here?
                if (player.hasPermission("keys.mod") || Keys.getStorageAdapter().ownsLock(player, block.getLocation().get())) {
                    // Remove locks
                    if (Keys.getStorageAdapter().removeLocks(block.getLocation().get())) {
                        // Build message
                        String blockName = block.getState().getType().getName().replace("minecraft:", "").replace("_", " ");
                        Vector3i position = block.getLocation().get().getPosition().toInt();
                        String message = String.format("Removed %s locks and keys at %d %d %d", blockName, position.getX(), position.getY(), position.getZ());

                        player.sendMessage(Format.heading(message));
                    }
                } else {
                    transaction.setValid(false);
                    player.sendMessage(Format.error("You may not destroy this locked location."));
                }

                return true;
            } catch (SQLException e) {
                player.sendMessage(Format.error("Storage error. Details have been logged."));
                e.printStackTrace();
            }
        }

        return false;
    }
}
