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
package com.helion3.keys.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.helion3.keys.Keys;
import com.helion3.keys.interaction.UnlockInteractionHandler;
import com.helion3.keys.util.Format;

public class UnlockCommand {
    private UnlockCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
            .description(Text.of("Unlocks a locked location."))
            .permission("keys.use")
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
                    if (!(source instanceof Player)) {
                        source.sendMessage(Format.error("Command usable only by a player."));
                        return CommandResult.empty();
                    }

                    Player player = (Player) source;

                    if (Keys.getInteractionHandler(player).isPresent()) {
                        Keys.removeInteractionHandler(player);
                        player.sendMessage(Format.message("Canceled unlock mode."));
                    } else {
                        Keys.registerInteractionHandler(player, new UnlockInteractionHandler());
                        player.sendMessage(Format.heading("Punch a locked block to unlock it..."));
                    }

                    return CommandResult.success();
                }
            }).build();
    }
}
