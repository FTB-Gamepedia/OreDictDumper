package com.gamepedia.ftb.oredictdumper.misc;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class ChatStyleColored extends ChatStyle {
    private final EnumChatFormatting color;

    public ChatStyleColored(EnumChatFormatting color) {
        this.color = color;
    }

    @Override
    public EnumChatFormatting getColor() {
        return color;
    }
}
