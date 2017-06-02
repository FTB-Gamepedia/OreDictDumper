package com.gamepedia.ftb.oredictdumper.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class OreDictOutputFormat {
    private final String extension;

    OreDictOutputFormat(String extension) {
        this.extension = extension;
    }

    @Nonnull
    public String getFileExtension() {
        return extension;
    }

    @Nonnull
    public abstract String parseEntries(List<OreDictEntry> entries);

    public static class JSONOutputFormat extends OreDictOutputFormat {
        public JSONOutputFormat() {
            super("json");
        }

        @Nonnull
        @Override
        public String parseEntries(List<OreDictEntry> entries) {
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
        public String parseEntries(List<OreDictEntry> entries) {
            StringBuilder builder = new StringBuilder();
            builder.append("Tag,ItemName,Metadata,ModID\n");
            for (OreDictEntry entry : entries) {
                builder.append(String.format("%s,%s,%s,%s\n", entry.getTagName(), entry.getDisplayName(), entry.getMetadata(), entry.getModID()));
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
        public String parseEntries(List<OreDictEntry> entries) {
            StringBuilder builder = new StringBuilder();
            for (OreDictEntry entry : entries) {
                builder.append(String.format("%s!%s!%s!\n", entry.getTagName(), entry.getDisplayName(), abbreviation));
            }
            return builder.toString();
        }
    }
}
