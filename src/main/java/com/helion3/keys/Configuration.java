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
package com.helion3.keys;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.block.BlockTypes;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Configuration {
    private final ConfigurationLoader<CommentedConfigurationNode> configManager;
    private ConfigurationNode rootNode = null;

    /**
     * Loads (creates new if needed) Configuration file.
     * @param defaultConfig
     * @param configManager
     */
    public Configuration(File defaultConfig, ConfigurationLoader<CommentedConfigurationNode> configManager) {
        this.configManager = configManager;

        try {
            // If file does not exist, we must create it
            if (!defaultConfig.exists()) {
                defaultConfig.getParentFile().mkdirs();
                defaultConfig.createNewFile();
                rootNode = configManager.createEmptyNode(ConfigurationOptions.defaults());
            } else {
                rootNode = configManager.load();
            }

            List<String> lockableblocks = new ArrayList<String>();
            lockableblocks.add(BlockTypes.ACACIA_DOOR.getId());
            lockableblocks.add(BlockTypes.ACACIA_FENCE_GATE.getId());
            lockableblocks.add(BlockTypes.ANVIL.getId());
            lockableblocks.add(BlockTypes.BEACON.getId());
            lockableblocks.add(BlockTypes.BIRCH_DOOR.getId());
            lockableblocks.add(BlockTypes.BIRCH_FENCE_GATE.getId());
            lockableblocks.add(BlockTypes.BREWING_STAND.getId());
            lockableblocks.add(BlockTypes.CHEST.getId());
            lockableblocks.add(BlockTypes.COMMAND_BLOCK.getId());
            lockableblocks.add(BlockTypes.DARK_OAK_DOOR.getId());
            lockableblocks.add(BlockTypes.DARK_OAK_FENCE_GATE.getId());
            lockableblocks.add(BlockTypes.DISPENSER.getId());
            lockableblocks.add(BlockTypes.DROPPER.getId());
            lockableblocks.add(BlockTypes.ENCHANTING_TABLE.getId());
            lockableblocks.add(BlockTypes.FENCE_GATE.getId());
            lockableblocks.add(BlockTypes.FURNACE.getId());
            lockableblocks.add(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE.getId());
            lockableblocks.add(BlockTypes.HOPPER.getId());
            lockableblocks.add(BlockTypes.IRON_DOOR.getId());
            lockableblocks.add(BlockTypes.IRON_TRAPDOOR.getId());
            lockableblocks.add(BlockTypes.JUKEBOX.getId());
            lockableblocks.add(BlockTypes.JUNGLE_DOOR.getId());
            lockableblocks.add(BlockTypes.JUNGLE_FENCE_GATE.getId());
            lockableblocks.add(BlockTypes.LEVER.getId());
            lockableblocks.add(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE.getId());
            lockableblocks.add(BlockTypes.SPRUCE_DOOR.getId());
            lockableblocks.add(BlockTypes.SPRUCE_FENCE_GATE.getId());
            lockableblocks.add(BlockTypes.STANDING_SIGN.getId());
            lockableblocks.add(BlockTypes.STONE_BUTTON.getId());
            lockableblocks.add(BlockTypes.STONE_PRESSURE_PLATE.getId());
            lockableblocks.add(BlockTypes.TRAPDOOR.getId());
            lockableblocks.add(BlockTypes.TRAPPED_CHEST.getId());
            lockableblocks.add(BlockTypes.WALL_BANNER.getId());
            lockableblocks.add(BlockTypes.WALL_SIGN.getId());
            lockableblocks.add(BlockTypes.WOODEN_BUTTON.getId());
            lockableblocks.add(BlockTypes.WOODEN_DOOR.getId());
            lockableblocks.add(BlockTypes.WOODEN_PRESSURE_PLATE.getId());

            ConfigurationNode lockable = rootNode.getNode("lockable", "blocks");
            if (lockable.isVirtual()) {
                lockable.setValue(lockableblocks);
            }

            ConfigurationNode autoLock = rootNode.getNode("autolock", "blocks");
            if (autoLock.isVirtual()) {
                autoLock.setValue(lockableblocks);
            }

            // Save
            try {
                configManager.save(rootNode);
            } catch(IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shortcut to rootNode.getNode().
     *
     * @param path Object[] Paths to desired node
     * @return ConfigurationNode
     */
    public ConfigurationNode getNode(Object... path) {
        return rootNode.getNode(path);
    }

    public void reload() throws IOException {
        rootNode = configManager.load();
    }
}
