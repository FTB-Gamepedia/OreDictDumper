package com.gamepedia.ftb.oredictdumper.misc;

public class OreDictEntry {
    public String tagName;
    public String displayName;
    public String modID;
    public int metadata;

    public OreDictEntry(String tag, String display, int meta, String mod) {
        this.tagName = tag;
        this.displayName = display;
        this.metadata = meta;
        this.modID = mod;
    }

    public String toString() {
        return String.format("%s:%s@%s (%s)", this.modID, this.displayName, this.metadata,
          this.tagName);
    }
}
