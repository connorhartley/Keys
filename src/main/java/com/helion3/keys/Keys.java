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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.helion3.keys.commands.KeysCommands;
import com.helion3.keys.commands.LockCommand;
import com.helion3.keys.commands.UnlockCommand;
import com.helion3.keys.interaction.InteractionHandler;
import com.helion3.keys.listeners.ChangeBlockListener;
import com.helion3.keys.listeners.ExplosionListener;
import com.helion3.keys.listeners.InteractBlockListener;
import com.helion3.keys.storage.H2StorageAdapter;
import com.helion3.keys.storage.StorageAdapter;

import ninja.leaping.configurate.Types;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "keys", name = "keys", version = "1.2")
public class Keys {
    private static ImmutableList<BlockType> autolockedBlocks;
    private static Configuration config;
    private static Game game;
    private static Map<Player, InteractionHandler> interactionHandlers = new HashMap<Player, InteractionHandler>();
    private static ImmutableList<BlockType> lockableBlocks;
    private static Logger logger;
    private static File parentDirectory;
    private static StorageAdapter storageAdapter;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        parentDirectory = defaultConfig.getParentFile();

        // Configuration
        config = new Configuration(defaultConfig, configManager);
        loadConfig();

        // Storage
        storageAdapter = new H2StorageAdapter();

        // Commands
        game.getCommandManager().register(this, KeysCommands.getCommand(), "keys");
        game.getCommandManager().register(this, LockCommand.getCommand(), "lock");
        game.getCommandManager().register(this, UnlockCommand.getCommand(), "unlock");

        // Listeners
        game.getEventManager().registerListeners(this, new ChangeBlockListener());
        game.getEventManager().registerListeners(this, new ExplosionListener());
        game.getEventManager().registerListeners(this, new InteractBlockListener());

        logger.info("Keys started. Calling in the key master. There is only zuul.");
    }

    /**
     * Load the configuration file.
     */
    private static void loadConfig() {
        // Lockable Blocks
        ImmutableList.Builder<BlockType> lockableBlockBuilder = ImmutableList.builder();
        List<String> blockIds = config.getNode("lockable", "blocks").getList(Types::asString);
        for (String blockId : blockIds) {
            Optional<BlockType> blockType = game.getRegistry().getType(BlockType.class, blockId);
            if (blockType.isPresent()) {
                lockableBlockBuilder.add(blockType.get());
            } else {
                logger.error("Invalid block id in the lockable.blocks configuration: " + blockId);
            }
        }
        lockableBlocks = lockableBlockBuilder.build();

        // Auto-lock Blocks
        ImmutableList.Builder<BlockType> autolockBlockBuilder = ImmutableList.builder();
        List<String> autolockBlockIds = config.getNode("autolock", "blocks").getList(Types::asString);
        for (String blockId : autolockBlockIds) {
            Optional<BlockType> blockType = game.getRegistry().getType(BlockType.class, blockId);
            if (blockType.isPresent()) {
                autolockBlockBuilder.add(blockType.get());
            } else {
                logger.error("Invalid block id in the autolock.blocks configuration: " + blockId);
            }
        }
        autolockedBlocks = autolockBlockBuilder.build();
    }

    public static void reload() {
        try {
            config.reload();
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /**
    *
    * @return
    */
   public static Game getGame() {
       return game;
   }

   /**
    * Injected Game instance.
    * @param injectGame Game
    */
   @Inject
   public void setGame(Game injectGame) {
       game = injectGame;
   }

   /**
    * Get blocks considered "lockable".
    * @return
    */
   public static ImmutableList<BlockType> getAutoLockedBlocks() {
       return autolockedBlocks;
   }

   /**
    * Get blocks considered "lockable".
    * @return
    */
   public static ImmutableList<BlockType> getLockableBlocks() {
       return lockableBlocks;
   }

   /**
    * Injects the Logger instance for this plugin
    * @param log Logger
    */
   @Inject
   private void setLogger(Logger log) {
       logger = log;
   }

   /**
    * Get an existing interaction handler for a player, if any.
    * @param player
    * @return Optional<BlockInteractionHandler>
    */
   public static Optional<InteractionHandler> getInteractionHandler(Player player) {
       return Optional.ofNullable(interactionHandlers.get(player));
   }

   /**
    * Get parent directory.
    * @return File
    */
   public static File getParentDirectory() {
       return parentDirectory;
   }

   /**
    * Register a new interaction handler for a specific player.
    * @param player
    * @param handler
    */
   public static void registerInteractionHandler(Player player, InteractionHandler handler) {
       interactionHandlers.put(player, handler);
   }

   /**
    * Remove an interaction handler for a player.
    * @param player
    */
   public static void removeInteractionHandler(Player player) {
       interactionHandlers.remove(player);
   }

   /**
    * Get storage adapter.
    * @return StorageAdapter
    */
   public static StorageAdapter getStorageAdapter() {
       return storageAdapter;
   }
}
