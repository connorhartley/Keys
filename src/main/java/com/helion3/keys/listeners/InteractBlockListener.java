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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.helion3.keys.locks.Lock;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

import com.helion3.keys.Keys;
import com.helion3.keys.interaction.InteractionHandler;
import com.helion3.keys.util.Format;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class InteractBlockListener {
    @Listener
    public void onUse(final InteractBlockEvent.Secondary event, @First Player player) {
        // Ignore clicks in the air
        if (event.getTargetBlock().equals(BlockSnapshot.NONE) || !event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        try {
            if (!player.hasPermission("keys.mod") && !Keys.getStorageAdapter().allowsAccess(player, event.getTargetBlock().getLocation().get())) {
                player.sendMessage(Format.error("You may not interact with this locked location."));
                event.setCancelled(true);
            }

            listLockOwner(player, event.getTargetBlock().getLocation().get());
        } catch (SQLException e) {
            player.sendMessage(Format.error("Storage error. Details have been logged."));
            e.printStackTrace();
        }
    }

    @Listener
    public void onPunchBlock(final InteractBlockEvent.Primary event, @First Player player) {
        // Ignore clicks in the air
        if (event.getTargetBlock().equals(BlockSnapshot.NONE) || !event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        Optional<InteractionHandler> optional = Keys.getInteractionHandler(player);
        if (!optional.isPresent()) {
            return;
        }

        optional.get().handle(player, event.getTargetBlock().getLocation().get());

        Keys.removeInteractionHandler(player);
    }

    protected void listLockOwner(Player player, Location<World> location) throws SQLException {
        if (player.hasPermission("keys.mod")) {
            List<Lock> masters = Keys.getStorageAdapter().getMasterLocks(location);

            for (Lock master : masters) {
                if (master.getUserId().equals(player.getUniqueId())) {
                    continue;
                }

                CompletableFuture<GameProfile> future = Keys.getGame().getServer().getGameProfileManager().get(master.getUserId());
                future.thenAccept((profile) -> {
                    player.sendMessage(Format.message("This block is locked by ", TextColors.LIGHT_PURPLE, profile.getName()));
                });
            }
        }
    }
}
