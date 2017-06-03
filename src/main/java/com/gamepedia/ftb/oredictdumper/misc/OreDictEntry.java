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

    @Override
    public String toString() {
        return String.format("%s:%s@%s (%s)", getModID(), getDisplayName(), getMetadata(), getTagName());
    }

    @Nonnull
    public String getTagName() {
        return tagName;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    @Nonnull
    public String getModID() {
        return modID;
    }

    public int getMetadata() {
        return metadata;
    }
}
