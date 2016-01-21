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

import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

import com.helion3.keys.Keys;
import com.helion3.keys.interaction.InteractionHandler;
import com.helion3.keys.util.Format;

public class InteractBlockListener {
    @Listener
    public void onOpenInventory(final InteractBlockEvent.Secondary event) {
        Optional<TileEntity> entity = event.getTargetBlock().getLocation().get().getTileEntity();
        if (!entity.isPresent()) {
            return;
        }

        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (!optionalPlayer.isPresent()) {
            return;
        }

        Player player = optionalPlayer.get();

        try {
            if (!player.hasPermission("keys.mod") && !Keys.getStorageAdapter().allowsAccess(player, event.getTargetBlock().getLocation().get())) {
                player.sendMessage(Format.error("You may not access this locked location."));
                event.setCancelled(true);
            }
        } catch (SQLException e) {
            player.sendMessage(Format.error("Storage error. Details have been logged."));
            e.printStackTrace();
        }
    }

    @Listener
    public void onPunchBlock(final InteractBlockEvent.Primary event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (!optionalPlayer.isPresent()) {
            return;
        }

        Player player = optionalPlayer.get();
        Optional<InteractionHandler> optional = Keys.getInteractionHandler(player);
        if (!optional.isPresent()) {
            return;
        }

        optional.get().handle(player, event.getTargetBlock().getLocation().get());

        Keys.removeInteractionHandler(player);
    }
}
