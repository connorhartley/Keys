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
package com.helion3.keys.storage;

import java.sql.SQLException;
import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.helion3.keys.locks.Lock;

public interface StorageAdapter {
    /**
     * Add a key for the given location, with player as original owner.
     *
     * @param player Player Owning player
     * @param location Location of block
     * @throws SQLException
     */
    void addKey(Player player, Location<World> location) throws SQLException;

    /**
     * Whether player owns a key for this location, or there are no locks.
     *
     * @param player Player accessing
     * @param location Location of lock
     * @return If no locks or player has a key
     * @throws SQLException
     */
    boolean allowsAccess(Player player, Location<World> location) throws SQLException;

    /**
     * List all locks for a given location.
     *
     * @param location
     * @return
     * @throws SQLException
     */
    List<Lock> getLocks(Location<World> location) throws SQLException;

    /**
     * Whether a player owns the lock on this location.
     *
     * @param player
     * @param location
     * @return
     * @throws SQLException
     */
    boolean ownsLock(Player player, Location<World> location) throws SQLException;

    /**
     * Remove a specific lock.
     *
     * @param player Owning Player of the lock.
     * @param location Location of lock.
     * @throws SQLException
     */
    void removeLock(Player player, Location<World> location) throws SQLException;

    /**
     * Remove all locks to a given location.
     *
     * @param location Location of locks
     * @return Whether locks were removed
     * @throws SQLException
     */
    boolean removeLocks(Location<World> location) throws SQLException;

    /**
     * Set a lock for the given location, with player as original owner.
     *
     * @param player Player Owning player
     * @param location Location of block
     * @throws SQLException
     */
    void setLock(Player player, Location<World> location) throws SQLException;
}
