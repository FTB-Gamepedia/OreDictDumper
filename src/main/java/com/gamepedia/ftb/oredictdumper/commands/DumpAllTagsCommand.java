package com.gamepedia.ftb.oredictdumper.commands;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.Set;

public class DumpAllTagsCommand extends TagDumpCommand {
    private static final DumpAllTagsCommand INSTANCE = new DumpAllTagsCommand();

    private DumpAllTagsCommand() {}

    @Nonnull
    @Override
    public String getName() {
        return "all";
    }

    @Nonnull
    @Override
    public Set<String> getFormats() {
        return ImmutableSet.of("csv", "json");
    }

    @Nonnull
    @Override
    protected String getFilename(CommandContext<CommandSource> context) {
        return "oredump";
    }

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        INSTANCE.registerInternal(commandDispatcher);
    }
}
