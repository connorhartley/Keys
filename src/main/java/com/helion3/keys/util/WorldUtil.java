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
package com.helion3.keys.util;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class WorldUtil {
    private WorldUtil() {}

    /**
     * Find a matching partner block. Must be the same type
     * and in one of the four cardinal directions.
     *
     * @param location Location of source block.
     * @return Optional Location of partner.
     */
    public static Optional<Location<World>> findPartnerBlock(Location<World> location) {
        BlockType type = location.getBlockType();

        Location<World> north = location.getRelative(Direction.NORTH);
        if (north.getBlockType().equals(type)) {
            return Optional.of(north);
        }

        Location<World> west = location.getRelative(Direction.WEST);
        if (west.getBlockType().equals(type)) {
            return Optional.of(west);
        }

        Location<World> south = location.getRelative(Direction.SOUTH);
        if (south.getBlockType().equals(type)) {
            return Optional.of(south);
        }

        Location<World> east = location.getRelative(Direction.EAST);
        if (east.getBlockType().equals(type)) {
            return Optional.of(east);
        }

        return Optional.empty();
    };
}
