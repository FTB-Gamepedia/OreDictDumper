package com.gamepedia.ftb.oredictdumper.misc;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public abstract class TagFormat {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Map<String, TagFormat> FORMATS = ImmutableMap.of(
            "csv", new CsvTagFormat(),
            "json", new JsonTagFormat(),
            "wiki", new WikiTagFormat()
    );

    @Nonnull
    private final String extension;

    private TagFormat(@Nonnull String extension) {
        this.extension = extension;
    }

    @Nonnull
    public final String getFileExtension() {
        return extension;
    }

    @Nonnull
    public abstract String parseEntries(@Nonnull List<TagEntry> entries, CommandContext<CommandSource> context);

    private static final class CsvTagFormat extends TagFormat {
        private CsvTagFormat() {
            super("csv");
        }

        @Nonnull
        @Override
        public String parseEntries(@Nonnull List<TagEntry> entries, CommandContext<CommandSource> context) {
            StringBuilder builder = new StringBuilder("Tag,ItemName,ModId,ItemId\n");

            for (TagEntry entry : entries) {
                builder.append(entry.tagName).append(',')
                        .append(entry.displayName).append(',')
                        .append(entry.modId).append(',')
                        .append(entry.itemId)
                        .append('\n');
            }

            return builder.toString();
        }
    }

    private static final class JsonTagFormat extends TagFormat {
        private JsonTagFormat() {
            super("json");
        }

        @Nonnull
        @Override
        public String parseEntries(@Nonnull List<TagEntry> entries, CommandContext<CommandSource> context) {
            return GSON.toJson(entries);
        }
    }

    private static final class WikiTagFormat extends TagFormat {
        private WikiTagFormat() {
            super("txt");
        }

        @Nonnull
        @Override
        public String parseEntries(@Nonnull List<TagEntry> entries, CommandContext<CommandSource> context) {
            String abbreviation = StringArgumentType.getString(context, "abbreviation");
            StringBuilder builder = new StringBuilder();

            for (TagEntry entry : entries) {
                builder.append(entry.tagName).append('!')
                        .append(entry.displayName).append('!')
                        .append(abbreviation).append('!')
                        .append('\n');
            }

            return builder.toString();
        }
    }
}
