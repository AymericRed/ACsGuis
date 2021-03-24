package fr.aym.acslib.services.error_tracking;

import net.minecraft.util.text.TextFormatting;

public enum TrackedErrorLevel {
    ADVICE(TextFormatting.AQUA), LOW(TextFormatting.GOLD), HIGH(TextFormatting.RED), FATAL(TextFormatting.DARK_RED);

    public final TextFormatting color;

    TrackedErrorLevel(TextFormatting color) {
        this.color = color;
    }
}
