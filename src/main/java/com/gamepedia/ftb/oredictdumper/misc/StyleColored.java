package com.gamepedia.ftb.oredictdumper.misc;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class StyleColored extends Style {
    private final TextFormatting color;

    public StyleColored(TextFormatting color) {
        this.color = color;
    }

    @Nullable
    @Override
    public TextFormatting getColor() {
        return color;
    }
}
