package com.gamepedia.ftb.oredictdumper.misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TagEntry {
    @Nonnull
    final String tagName;
    @Nonnull
    final String displayName;
    @Nullable
    final String itemId;
    @Nonnull
    final String modId;

    public TagEntry(@Nonnull String tagName, @Nonnull String displayName, @Nullable String itemId, @Nonnull String modId) {
        this.tagName = tagName;
        this.displayName = displayName;
        this.itemId = itemId;
        this.modId = modId;
    }

    @Override
    public String toString() {
        return String.format("%s:%s (%s)", modId, displayName, tagName);
    }
}
