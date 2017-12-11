package com.gamepedia.ftb.oredictdumper.misc;

import javax.annotation.Nonnull;

public class OreDictEntry {
    @Nonnull
    final String tagName;
    @Nonnull
    final String displayName;
    @Nonnull
    final String modID;
    final int metadata;

    public OreDictEntry(@Nonnull String tag, @Nonnull String display, int meta, @Nonnull String mod) {
        tagName = tag;
        displayName = display;
        metadata = meta;
        modID = mod;
    }

    @Override
    public String toString() {
        return String.format("%s:%s@%s (%s)", modID, displayName, metadata, tagName);
    }
}
