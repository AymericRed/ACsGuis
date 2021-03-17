package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Layouts automatically compute position, width and height of all elements added into the GuiPanel using this layout <br>
 *     The computed properties must be set to "auto" in the css sheets
 *
 * @see GridLayout
 */
public interface PanelLayout<T extends ComponentStyleManager> extends AutoStyleHandler<T>
{
    @Override
    default Priority getPriority(T forT) {
        return Priority.LAYOUT;
    }

    @Override
    default boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, T target) {
        if(property == EnumCssStyleProperties.TOP)
        {
            int val = getY(target);
            if(val != target.getRenderY())
            {
                target.getYPos().setAbsolute(val, GuiConstants.ENUM_RELATIVE_POS.START);
            }
            return true;
        }
        else if(property == EnumCssStyleProperties.LEFT)
        {
            int val = getX(target);
            if(val != target.getRenderX())
            {
                target.getXPos().setAbsolute(val, GuiConstants.ENUM_RELATIVE_POS.START);
            }
            return true;
        }
        else if(property == EnumCssStyleProperties.WIDTH)
        {
            int val = getWidth(target);
            if(val != target.getRenderWidth())
            {
                target.getWidth().setAbsolute(val);
            }
            return true;
        }
        else if(property == EnumCssStyleProperties.HEIGHT)
        {
            int val = getHeight(target);
            if(val != target.getRenderHeight())
            {
                target.getHeight().setAbsolute(val);
            }
            return true;
        }
        return false;
    }

    List<EnumCssStyleProperties> modifiedProperties = Arrays.asList(EnumCssStyleProperties.TOP, EnumCssStyleProperties.LEFT, EnumCssStyleProperties.WIDTH, EnumCssStyleProperties.HEIGHT);

    @Override
    default List<EnumCssStyleProperties> getModifiedProperties(T target) {
        return modifiedProperties;
    }

    int getX(T target);
    int getY(T target);
    int getWidth(T target);
    int getHeight(T target);

    /**
     * Resets the layouts
     */
    void clear();
}
