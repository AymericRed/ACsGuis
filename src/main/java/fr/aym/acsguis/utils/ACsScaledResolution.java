package fr.aym.acsguis.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ACsScaledResolution {
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public ACsScaledResolution(Minecraft minecraftClient) {
        this(minecraftClient, minecraftClient.displayWidth, minecraftClient.displayHeight, true);
    }

    public ACsScaledResolution(Minecraft minecraftClient, int displayWidth, int displayHeight, boolean applyMcScale) {
        this.scaledWidth = displayWidth;
        this.scaledHeight = displayHeight;
        this.scaleFactor = 1;

        if (applyMcScale) {
            boolean flag = minecraftClient.isUnicode();
            int i = minecraftClient.gameSettings.guiScale;

            if (i == 0) {
                i = 1000;
            }

            while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
                ++this.scaleFactor;
            }

            if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
                --this.scaleFactor;
            }
        }

        double scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        double scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public int getScaleFactor() {
        return this.scaleFactor;
    }
}
