package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.utils.GuiConstants;

import java.util.HashMap;
import java.util.Map;

public class FlowLayout implements PanelLayout<InternalComponentStyle> {
    private final Map<GuiComponent, ComponentPosition> cache = new HashMap<>();
    private float currentX;
    private float lastWidth;
    private float currentY;
    private float lastHeight;
    private GuiConstants.COMPONENT_DISPLAY lastDisplay;
    private GuiPanel container;

    public void placeElement(InternalComponentStyle target) {
        if (target.getDisplay() == GuiConstants.COMPONENT_DISPLAY.INLINE) {
            if (currentX + target.getRenderWidth() > container.getWidth()) {
                currentX = 0;
                currentY += lastHeight;
            }
        } else if (target.getDisplay() == GuiConstants.COMPONENT_DISPLAY.BLOCK && lastDisplay == GuiConstants.COMPONENT_DISPLAY.INLINE) {
            currentX = 0;
            currentY += lastHeight;
        }
        // System.out.println("TPlace element: " + target + " " + lastWidth + " " + lastHeight + " " + currentX + " " + currentY + " " + target.getWidth().getValue().getRawValue());
        ComponentPosition pos = new ComponentPosition(currentX, currentY);
        cache.put(target.getOwner(), pos);
        if (target.getDisplay() != GuiConstants.COMPONENT_DISPLAY.NONE) {
            lastWidth = target.getRenderWidth();
            lastHeight = target.getRenderHeight();
        }
        switch (target.getDisplay()) {
            case BLOCK:
                currentX = 0;
                currentY += lastHeight;
                break;
            case INLINE:
                currentX += lastWidth;
                //TODO C QUOI LA DIFF AVEC INLINE_BLOCK ??
                break;
        }
        lastDisplay = target.getDisplay();
    }

    @Override
    public float getX(InternalComponentStyle target) {
        target.getXPos().setPositionFunction((style, xPos) -> {
            if (!cache.containsKey(style.getOwner()))
                placeElement(style);
            xPos.setAbsolute(cache.get(style.getOwner()).x);
        });
        return 0;
    }

    @Override
    public float getY(InternalComponentStyle target) {
        target.getYPos().setPositionFunction((style, yPos) -> {
            if (!cache.containsKey(style.getOwner()))
                placeElement(style);
            yPos.setAbsolute(cache.get(style.getOwner()).y);
        });
        return 0;
    }

    @Override
    public float getWidth(InternalComponentStyle target) {
        throw new UnsupportedOperationException("Flow layout does not support width computation");
    }

    @Override
    public float getHeight(InternalComponentStyle target) {
        throw new UnsupportedOperationException("Flow layout does not support height computation");
    }

    private final EnumCssStyleProperty[] modifiedProperties = {EnumCssStyleProperty.TOP, EnumCssStyleProperty.LEFT};

    @Override
    public EnumCssStyleProperty[] getModifiedProperties() {
        return modifiedProperties;
    }

    @Override
    public void clear() {
        cache.clear();
        currentX = 0;
        currentY = 0;
        lastWidth = 0;
        lastHeight = 0;
    }

    @Override
    public void setContainer(GuiPanel container) {
        if (this.container != null)
            throw new IllegalArgumentException("Layout already used in " + this.container);
        this.container = container;
    }

    public static class ComponentPosition {
        public float x, y;
        public float width, height;

        public ComponentPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
