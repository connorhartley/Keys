package com.helion3.keys.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.format.TextColors;

import com.helion3.keys.util.Format;

public class HelpCommand {
    private HelpCommand(){}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
                    source.sendMessage(Format.message("/lock", TextColors.GRAY, " - Lock a block manually."));
                    source.sendMessage(Format.message("/unlock", TextColors.GRAY, " - Unlock an block."));
                    source.sendMessage(Format.message("/keys add [player]", TextColors.GRAY, " - Add a player to a locked block."));
                    source.sendMessage(Format.message("/keys del [player]", TextColors.GRAY, " - Remove a player's access to a locked block."));
                    source.sendMessage(Format.message("/keys reload", TextColors.GRAY, " - Reload configuration."));
                    return CommandResult.empty();
                }
            }).build();
    }
}