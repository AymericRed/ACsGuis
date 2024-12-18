package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.positionning.Size.SizeValue;
import fr.aym.acsguis.utils.GuiConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple grid layout
 *
 * @see BorderedGridLayout
 */
public class GridLayout implements PanelLayout<InternalComponentStyle> {
    private final Map<ComponentStyle, Integer> cache = new HashMap<>();
    private int nextIndex;

    private final SizeValue width, height, spacing;
    private final GridDirection direction;
    private final int elementsPerLine;
    private GuiPanel container;

    /**
     * @param width           Tile width, in pixels
     * @param height          Tile height, in pixels
     * @param spacing         Space between tiles, in all directions, in pixels
     * @param direction       Primary direction of the alignment (direction of a "line")
     * @param elementsPerLine Number of elements on each "lines", use -1 to automatically fill the lines
     */
    public GridLayout(int width, int height, int spacing, GridDirection direction, int elementsPerLine) {
        this(new SizeValue(width, GuiConstants.ENUM_SIZE.ABSOLUTE), new SizeValue(height, GuiConstants.ENUM_SIZE.ABSOLUTE), new SizeValue(spacing, GuiConstants.ENUM_SIZE.ABSOLUTE), direction, elementsPerLine);
        // Retro-compatibility (-1 was 100%)
        if (width == -1)
            this.width.setRelative(1, CssValue.Unit.RELATIVE_TO_PARENT);
        if (height == -1)
            this.height.setRelative(1, CssValue.Unit.RELATIVE_TO_PARENT);
    }

    /**
     * @param width           Tile width, relative between 0 and 1 (0.5 = 50%)
     * @param height          Tile height, relative between 0 and 1 (0.5 = 50%)
     * @param spacing         Space between tiles, in all directions, relative between 0 and 1 (0.5 = 50%)
     * @param direction       Primary direction of the alignment (direction of a "line")
     * @param elementsPerLine Number of elements on each "lines", use -1 to automatically fill the lines
     */
    public GridLayout(float width, float height, float spacing, GridDirection direction, int elementsPerLine) {
        this(new SizeValue(width, GuiConstants.ENUM_SIZE.RELATIVE), new SizeValue(height, GuiConstants.ENUM_SIZE.RELATIVE), new SizeValue(spacing, GuiConstants.ENUM_SIZE.RELATIVE), direction, elementsPerLine);
    }

    /**
     * @param width           Tile width
     * @param height          Tile height
     * @param spacing         Space between tiles, in all directions
     * @param direction       Primary direction of the alignment (direction of a "line")
     * @param elementsPerLine Number of elements on each "lines", use -1 to automatically fill the lines
     */
    public GridLayout(SizeValue width, SizeValue height, SizeValue spacing, GridDirection direction, int elementsPerLine) {
        this.width = width;
        this.height = height;
        this.spacing = spacing;
        this.direction = direction;
        this.elementsPerLine = elementsPerLine;
    }

    /**
     * Creates a simple column layout with one element per line
     *
     * @param height  Element height, in pixels
     * @param spacing Space between elements, in pixels
     * @return A new column layout
     */
    public static GridLayout columnLayout(int height, int spacing) {
        return new GridLayout(new SizeValue(1, GuiConstants.ENUM_SIZE.RELATIVE), new SizeValue(height, GuiConstants.ENUM_SIZE.ABSOLUTE), new SizeValue(spacing, GuiConstants.ENUM_SIZE.ABSOLUTE), GridDirection.HORIZONTAL, 1);
    }

    /**
     * Creates a simple column layout with one element per line
     *
     * @param height  Element height, relative between 0 and 1 (0.5 = 50%)
     * @param spacing Space between elements, relative between 0 and 1 (0.5 = 50%)
     * @return A new column layout
     */
    public static GridLayout columnLayout(float height, float spacing) {
        return new GridLayout(new SizeValue(1, GuiConstants.ENUM_SIZE.RELATIVE), new SizeValue(height, GuiConstants.ENUM_SIZE.RELATIVE), new SizeValue(spacing, GuiConstants.ENUM_SIZE.RELATIVE), GridDirection.HORIZONTAL, 1);
    }

    @Override
    public float getX(InternalComponentStyle target) {
        if (!cache.containsKey(target)) {
            cache.put(target, nextIndex);
            nextIndex++;
        }
        int elementsPerLine = this.elementsPerLine;
        if (direction == GridDirection.HORIZONTAL && elementsPerLine == -1) {
            elementsPerLine = (int) (target.getParent().getRenderWidth() / getWidth(target));
        }
        float spacing = this.spacing.computeValue(container.getWidth(), container.getHeight(), container.getWidth());
        float width = getWidth(target);
        return direction == GridDirection.HORIZONTAL ? (width + spacing) * (cache.get(target) % elementsPerLine) : (width + spacing) * (cache.get(target) / elementsPerLine);
    }

    @Override
    public float getY(InternalComponentStyle target) {
        if (!cache.containsKey(target)) {
            cache.put(target, nextIndex);
            nextIndex++;
        }
        int elementsPerLine = this.elementsPerLine;
        if (direction == GridDirection.VERTICAL && elementsPerLine == -1) {
            elementsPerLine = (int) (target.getParent().getRenderHeight() / getHeight(target));
        }
        float spacing = this.spacing.computeValue(container.getWidth(), container.getHeight(), container.getHeight());
        float height = getHeight(target);
        return direction == GridDirection.VERTICAL ? (height + spacing) * (cache.get(target) % elementsPerLine) : (height + spacing) * (cache.get(target) / elementsPerLine);
    }

    @Override
    public float getWidth(InternalComponentStyle target) {
        GuiFrame frame = target.getOwner().getGui().getFrame();
        return width.computeValue(frame.getResolution().getScaledWidth(), frame.getResolution().getScaledHeight(), container.getWidth());
    }

    @Override
    public float getHeight(InternalComponentStyle target) {
        GuiFrame frame = target.getOwner().getGui().getFrame();
        return height.computeValue(frame.getResolution().getScaledWidth(), frame.getResolution().getScaledHeight(), container.getHeight());
    }

    @Override
    public void clear() {
        cache.clear();
        nextIndex = 0;
    }

    @Override
    public void setContainer(GuiPanel container) {
        if (this.container != null)
            throw new IllegalArgumentException("Layout already used in " + this.container);
        this.container = container;
    }

    public enum GridDirection {
        HORIZONTAL, VERTICAL
    }
}
