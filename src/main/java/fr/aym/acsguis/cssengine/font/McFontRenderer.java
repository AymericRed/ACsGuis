package fr.aym.acsguis.cssengine.font;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IResourceManager;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.font.effects.ShadowEffect;

import java.util.Collection;
import java.util.List;

/**
 * Default Minecraft font renderer, only supports shadow effect
 */
public class McFontRenderer implements ICssFont
{
    private FontRenderer renderer;
    private boolean shadow;

    public McFontRenderer() {}

    @Override
    public void load(IResourceManager resourceManager)
    {
        renderer = Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public void pushEffects(Collection<Effect> effectList) {
        for(Effect e : effectList) {
            if(e instanceof ShadowEffect)
            {
                shadow = true;
                break;
            }
        }
    }

    @Override
    public void draw(float x, float y, String text, int color) {
        renderer.drawString(text, x, y, color, shadow);
    }

    @Override
    public void popEffects() {
        shadow = false;
    }

    @Override
    public int getHeight(String text) {
        return renderer.FONT_HEIGHT;
    }

    @Override
    public int getWidth(String text) {
        return renderer.getStringWidth(text);
    }

    @Override
    public List<String> trimTextToWidth(String text, int maxWidth, int maxTextHeight) {
        return GuiAPIClientHelper.trimTextToWidth(text, maxWidth, maxTextHeight);
    }
}
