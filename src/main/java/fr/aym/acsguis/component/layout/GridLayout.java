package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.component.style.ComponentStyleManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple grid layout
 *
 * @see BorderedGridLayout
 */
public class GridLayout implements PanelLayout<ComponentStyleManager>
{
    private final Map<ComponentStyleManager, Integer> cache = new HashMap<>();
    private int nextIndex;

    private final int width, height, spacing;
    private final GridDirection direction;
    private final int elementsPerLine;

    /**
     * @param width Tile width, use -1 to use parent width
     * @param height Tile height, use -1 to use parent height
     * @param spacing Space between tiles, in all directions
     * @param direction Primary direction of the alignment (direction of a "line")
     * @param elementsPerLine Number of elements on each "lines", use -1 to automatically fill the lines
     */
    public GridLayout(int width, int height, int spacing, GridDirection direction, int elementsPerLine) {
        this.width = width;
        this.height = height;
        this.spacing = spacing;
        this.direction = direction;
        this.elementsPerLine = elementsPerLine;
        //System.out.println("Inited LAYOUT "+width+" "+height+" "+spacing+" "+direction+" "+elementsPerLine);
    }

    @Override
    public int getX(ComponentStyleManager target) {
        if(!cache.containsKey(target)) {
            cache.put(target, nextIndex);
            nextIndex++;
        }
        int elementsPerLine = this.elementsPerLine;
        if(direction == GridDirection.HORIZONTAL && elementsPerLine == -1) {
            elementsPerLine = target.getParent().getRenderWidth()/getWidth(target);
        }
        return direction == GridDirection.HORIZONTAL ? (getWidth(target.getParent().getRenderWidth())+spacing)*(cache.get(target)%elementsPerLine) : (getWidth(target.getParent().getRenderWidth())+spacing)*(cache.get(target)/elementsPerLine);
    }

    @Override
    public int getY(ComponentStyleManager target) {
        if(!cache.containsKey(target)) {
            cache.put(target, nextIndex);
            nextIndex++;
        }
        int elementsPerLine = this.elementsPerLine;
        if(direction == GridDirection.VERTICAL && elementsPerLine == -1) {
            elementsPerLine = target.getParent().getRenderHeight()/getHeight(target);
        }
        return direction == GridDirection.VERTICAL ? (getHeight(target.getParent().getRenderHeight())+spacing)*(cache.get(target)%elementsPerLine) : (getHeight(target.getParent().getRenderHeight())+spacing)*(cache.get(target)/elementsPerLine);
    }

    @Override
    public int getWidth(ComponentStyleManager target) {
        return getWidth(target.getParent().getRenderWidth());
    }

    @Override
    public int getHeight(ComponentStyleManager target) {
        return getHeight(target.getParent().getRenderHeight());
    }

    @Override
    public void clear() {
        cache.clear();
        nextIndex = 0;
    }

    public int getWidth(int parentWidth) {
        return width == -1 ? parentWidth : width;
    }

    public int getHeight(int parentHeight) {
        return height == -1 ? parentHeight : height;
    }

    public enum GridDirection
    {
        HORIZONTAL, VERTICAL
    }
}
