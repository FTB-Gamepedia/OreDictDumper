package com.gamepedia.ftb.oredictdumper.misc;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.Nonnull;

public abstract class OreDictOutputFormat {
    @Nonnull
    private final String extension;

    OreDictOutputFormat(@Nonnull String extension) {
        this.extension = extension;
    }

    @Nonnull
    public String getFileExtension() {
        return extension;
    }

    @Nonnull
    public abstract String parseEntries(ImmutableList<OreDictEntry> entries);

    public static class JSONOutputFormat extends OreDictOutputFormat {
        public JSONOutputFormat() {
            super("json");
        }

        @Nonnull
        @Override
        public String parseEntries(ImmutableList<OreDictEntry> entries) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(entries);
        }
    }

    public static class CSVOutputFormat extends OreDictOutputFormat {
        public CSVOutputFormat() {
            super("csv");
        }

        @Nonnull
        @Override
        public String parseEntries(ImmutableList<OreDictEntry> entries) {
            StringBuilder builder = new StringBuilder();
            builder.append("Tag,ItemName,Metadata,ModID\n");
            for (OreDictEntry entry : entries) {
                builder.append(String.format("%s,%s,%s,%s\n", entry.tagName, entry.displayName, entry.metadata, entry.modID));
            }
            return builder.toString();
        }
    }

    public static class WikiOutputFormat extends OreDictOutputFormat {
        private final String abbreviation;

        public WikiOutputFormat(String abbreviation) {
            super("txt");
            this.abbreviation = abbreviation;
        }

        @Nonnull
        @Override
        public String parseEntries(ImmutableList<OreDictEntry> entries) {
            StringBuilder builder = new StringBuilder();
            for (OreDictEntry entry : entries) {
                builder.append(String.format("%s!%s!%s!\n", entry.tagName, entry.displayName, abbreviation));
            }
            return builder.toString();
        }
    }
}
