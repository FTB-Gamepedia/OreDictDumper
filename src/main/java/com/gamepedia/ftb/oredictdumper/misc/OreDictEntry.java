package com.gamepedia.ftb.oredictdumper.misc;

import javax.annotation.Nonnull;

public class OreDictEntry {
    @Nonnull
    private final String tagName;
    @Nonnull
    private final String displayName;
    @Nonnull
    private final String modID;
    private final int metadata;

    public OreDictEntry(@Nonnull String tag, @Nonnull String display, int meta, @Nonnull String mod) {
        tagName = tag;
        displayName = display;
        metadata = meta;
        modID = mod;
    }

    @Nonnull
    String getTagName() {
        return tagName;
    }

    @Nonnull
    String getDisplayName() {
        return displayName;
    }

    @Nonnull
    String getModID() {
        return modID;
    }

    int getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return String.format("%s:%s@%s (%s)", getModID(), getDisplayName(), getMetadata(), getTagName());
    }
}
