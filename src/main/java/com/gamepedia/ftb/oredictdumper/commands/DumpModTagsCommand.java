package com.gamepedia.ftb.oredictdumper.commands;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class DumpModTagsCommand extends TagDumpCommand {
    private static final DumpModTagsCommand INSTANCE = new DumpModTagsCommand();

    private DumpModTagsCommand() {}

    @Nonnull
    @Override
    public String getName() {
        return "mod";
    }

    @Nonnull
    @Override
    public Set<String> getFormats() {
        return ImmutableSet.of("csv", "json");
    }

    @Nonnull
    @Override
    protected String getFilename(CommandContext<CommandSource> context) {
        return Objects.requireNonNull(getNamespace(context));
    }

    @Override
    protected RequiredArgumentBuilder<CommandSource, String> modifyBaseBuilder(LiteralArgumentBuilder<CommandSource> builder) {
        return argument("namespace", StringArgumentType.word())
                .suggests((context, suggestionsBuilder) -> {
                    ITextComponent message = new TranslationTextComponent("commands.dumptags.namespace.tooltip");
                    for (String namespace : namespaces.keySet()) {
                        suggestionsBuilder.suggest(namespace, message);
                    }

                    return suggestionsBuilder.buildFuture();
                })
                .then(literal("wiki")
                        .then(argument("abbreviation", StringArgumentType.word())
                                .executes(context -> {
                                    executeDump(context, "wiki", getNamespace(context));

                                    return 1;
                                })));
    }

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        INSTANCE.registerInternal(commandDispatcher);
    }
}
