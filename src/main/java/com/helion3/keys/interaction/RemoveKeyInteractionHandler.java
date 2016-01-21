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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.helion3.keys.Keys;
import com.helion3.keys.util.Format;

public class RemoveKeyInteractionHandler implements InteractionHandler {
    private final Player recipient;

    public RemoveKeyInteractionHandler(Player recipient) {
        this.recipient = recipient;
    }

    @Override
    public void handle(Player player, Location<World> location) {
        try {
            if (Keys.getStorageAdapter().getLocks(location).isEmpty()) {
                player.sendMessage(Format.error("This block isn't locked."));
            } else {
                if (player.hasPermission("keys.mod") || Keys.getStorageAdapter().ownsLock(player, location)) {
                    // Add a key
                    Keys.getStorageAdapter().addKey(recipient, location);
                    player.sendMessage(Format.success(String.format("Key added for %s", recipient.getName())));
                } else {
                    player.sendMessage(Format.error("Cannot unlock, you do not own this lock."));
                }
            }
        } catch(SQLException e) {
            player.sendMessage(Format.error("Storage error. Details have been logged."));
        }
    }
}
