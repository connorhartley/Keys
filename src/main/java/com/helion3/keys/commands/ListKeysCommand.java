package com.helion3.keys.commands;

public class ListKeysCommand {
    private ListKeysCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
            .arguments(GenericArguments.string(Text.of("user")))
            .permission("keys.use")
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
                    Optional<String> user = args.<String>getOne("user");
                    if (user.isPresent()) {
                        ListenableFuture<GameProfile> profile = Keys.getGame().getServer().getGameProfileManager().get(user.get());
                        profile.addListener(new Runnable() {
                            @Override
                            public void run() {
                                List<Lock> locks = Keys.getStorageAdapter().listLocks(profile, true);
                                displayLocks(source, locks);
                            }
                        }, MoreExecutors.sameThreadExecutor());
                    }

                    return CommandResult.empty();
                }
            }).build();
    }

    private static void displayLocks(CommandSource source, GameProfile profile, List<Lock> locks) {
        for (Lock lock : locks) {

        }
    }
}
