package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.cssengine.style.ComponentStyleManager;

/**
 * A simple grid layout, with left and top paddings
 *
 * @see GridLayout
 */
public class BorderedGridLayout extends GridLayout
{
    private final int paddingLeft, paddingTop;

    /**
     * @param width Tile width, use -1 to use parent width
     * @param height Tile height, use -1 to use parent height
     * @param spacing Space between tiles, in all directions
     * @param direction Primary direction of the alignment (direction of a "line")
     * @param elementsPerLine Number of elements on each "lines"
     * @param paddingLeft Left padding
     * @param paddingTop Top padding
     */
    public BorderedGridLayout(int width, int height, int spacing, GridDirection direction, int elementsPerLine, int paddingLeft, int paddingTop) {
        super(width, height, spacing, direction, elementsPerLine);
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
    }

    @Override
    public int getX(ComponentStyleManager target) {
       return super.getX(target)+paddingLeft;
    }

    @Override
    public int getY(ComponentStyleManager target) {
      return super.getY(target)+paddingTop;
    }
}
