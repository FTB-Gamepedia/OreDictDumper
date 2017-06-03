package com.gamepedia.ftb.oredictdumper.misc;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class StyleColored extends Style {
    private final TextFormatting color;

    public StyleColored(TextFormatting color) {
        this.color = color;
    }

    @Override
    public TextFormatting getColor() {
        return color;
    }
}
