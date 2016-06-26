package com.gamepedia.ftb.oredictdumper.misc;

public class OreDictEntry {
    public String tagName;
    public String displayName;
    public String modID;
    public int metadata;

    public OreDictEntry(String tag, String display, int meta, String mod) {
        tagName = tag;
        displayName = display;
        metadata = meta;
        modID = mod;
    }

    public String toString() {
        return String.format("%s:%s@%s (%s)", modID, displayName, metadata, tagName);
    }
}
