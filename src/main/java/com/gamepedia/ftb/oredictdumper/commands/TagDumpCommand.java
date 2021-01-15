package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.OreDictDumper;
import com.gamepedia.ftb.oredictdumper.misc.TagEntry;
import com.gamepedia.ftb.oredictdumper.misc.TagFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public abstract class TagDumpCommand {
    @Nonnull
    public abstract String getName();

    @Nonnull
    public abstract Set<String> getFormats();

    @Nonnull
    protected abstract String getFilename(CommandContext<CommandSource> context);

    protected ArgumentBuilder<CommandSource, ?> modifyBaseBuilder(LiteralArgumentBuilder<CommandSource> builder) {
        return builder;
    }

    private final FormatArgumentType formatType = new FormatArgumentType(getFormats());
    protected Map<String, ModContainer> namespaces;

    protected final void registerInternal(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralArgumentBuilder<CommandSource> sub = literal(this.getName());
        namespaces = ModList.get().applyForEachModContainer(Function.identity())
                .filter(mod -> mod.getNamespace() != null)
                .collect(Collectors.toMap(ModContainer::getNamespace, Function.identity()));

        ArgumentBuilder<CommandSource, ?> modified = modifyBaseBuilder(sub)
                .then(argument("format", formatType)
                        .executes(context -> {
                            executeDump(context, StringArgumentType.getString(context, "format").toLowerCase(), getNamespace(context));

                            return 1;
                        }));
        if (modified != sub)
            sub.then(modified);
        commandDispatcher.register(literal("dumptags").then(sub));
    }

    protected final void executeDump(CommandContext<CommandSource> context, String format, String namespace) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        List<TagEntry> entries = getEntries(namespace);
        if (entries.isEmpty()) {
            // The namespace isn't valid if this is empty as vanilla always adds some tags
            throw new SimpleCommandExceptionType(new TranslationTextComponent("commands.dumptags.namespace.invalid", namespace)).create();
        }

        TagFormat tagFormat = TagFormat.FORMATS.get(format);
        String filename = this.getFilename(context);
        String ext = tagFormat.getFileExtension();

        Path relative = Paths.get("dumps", String.format("%s.%s", filename, ext));
        Path dir = source.getWorld().getServer().getDataDirectory().getAbsoluteFile().toPath().normalize();
        Path complete = dir.resolve(relative);

        try {
            Files.createDirectories(complete.getParent());
            Files.write(complete, tagFormat.parseEntries(entries, context).getBytes(StandardCharsets.UTF_8));

            ITextComponent message = new TranslationTextComponent("commands.dumptags.success", entries.size(), relative)
                    .mergeStyle(TextFormatting.GREEN);
            source.sendFeedback(message, false);
        } catch (IOException e) {
            OreDictDumper.LOGGER.error("Error while dumping tag entries {}", entries, e);

            ITextComponent message = new TranslationTextComponent("commands.dumptags.exception", relative);
            source.sendErrorMessage(message);
        }
    }

    private List<TagEntry> getEntries(String namespace) {
        List<TagEntry> entries = new ArrayList<>();

        addEntries(namespace, entries, ItemTags.getAllTags(), Function.identity());
        addEntries(namespace, entries, BlockTags.getAllTags(), Block::asItem);

        return entries;
    }

    private <T extends ForgeRegistryEntry<T>> void addEntries(String namespace, List<TagEntry> entries, List<? extends ITag.INamedTag<T>> tags, Function<T, Item> func) {
        for (ITag.INamedTag<T> tag : tags) {
            String tagName = tag.getName().toString();

            for (T registryItem : tag.getAllElements()) {
                ResourceLocation resource = registryItem.getRegistryName();

                if (resource == null || (namespace != null && !namespace.equalsIgnoreCase(resource.getNamespace())))
                    continue;

                String modId = namespaces.get(resource.getNamespace()).getModId();
                entries.add(new TagEntry(tagName, I18n.format(func.apply(registryItem).getTranslationKey()), resource.toString(), modId));
            }
        }
    }

    protected final String getNamespace(CommandContext<CommandSource> context) {
        try {
            return StringArgumentType.getString(context, "namespace");
        } catch (IllegalArgumentException e) {
            // Swallow
            return null;
        }
    }

    private static final class FormatArgumentType implements ArgumentType<String> {
        private final Set<String> formats;

        private FormatArgumentType(Set<String> formats) {
            this.formats = formats;
        }

        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            String str = reader.readUnquotedString().toLowerCase();
            if (!formats.contains(str)) {
                String possible = String.join(", ", formats);
                throw new SimpleCommandExceptionType(new TranslationTextComponent("commands.dumptags.no_parse", possible)).create();
            }

            return str;
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            for (String format : formats) {
                builder.suggest(format);
            }

            return builder.buildFuture();
        }
    }
}
