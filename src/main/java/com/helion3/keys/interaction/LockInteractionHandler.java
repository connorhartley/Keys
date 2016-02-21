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
package com.helion3.keys.interaction;

import java.sql.SQLException;
import java.util.Optional;

import com.helion3.keys.util.WorldUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.helion3.keys.Keys;
import com.helion3.keys.util.Format;

public class LockInteractionHandler implements InteractionHandler {
    @Override
    public void handle(Player player, Location<World> location) {
        try {
            if (!Keys.getLockableBlocks().contains(location.getBlock().getType())) {
                player.sendMessage(Format.error("You may not lock a block of this type."));
                return;
            }

            if (!Keys.getStorageAdapter().getLocks(location).isEmpty()) {
                player.sendMessage(Format.error("This block is already locked."));
            } else {
                // Lock this block
                Keys.getStorageAdapter().setLock(player, location);
                player.sendMessage(Format.heading("Successfully locked!"));

                // Lock partner
                Optional<Location<World>> partner = WorldUtil.findPartnerBlock(location);
                if (partner.isPresent()) {
                    player.sendMessage(Text.of(TextColors.GRAY, "Locking partner location too..."));
                    Keys.getStorageAdapter().setLock(player, partner.get());
                }
            }
        } catch(SQLException e) {
            player.sendMessage(Format.error("Storage error. Details have been logged."));
        }
    }
}