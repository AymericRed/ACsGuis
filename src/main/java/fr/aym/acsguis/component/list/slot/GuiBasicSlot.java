package fr.aym.acsguis.component.list.slot;

import fr.aym.acsguis.component.list.GuiList;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssIntValue;
import fr.aym.acsguis.utils.GuiConstants;

import java.awt.*;

import static fr.aym.acsguis.cssengine.parsing.core.objects.CssValue.Unit.RELATIVE_TO_PARENT;

public class GuiBasicSlot extends GuiSlot {
    protected GuiLabel entryNameLabel;

    public GuiBasicSlot(GuiList list, int entryId, String entryName) {
        super(list, entryId, entryName);

        //TODO DEFAULT STYLE IN CSS
        getStyleCustomizer().setRelativeX(0.25f, RELATIVE_TO_PARENT).setRelativeWidth(0.5f, RELATIVE_TO_PARENT)
                .setYPos(30 * entryId + list.getListPaddingTop()).setHeight(25);

        getStyleCustomizer().setBackgroundColor(new Color(0, 0, 0, 0.3f).getRGB())
                .setBorderSize(new CssIntValue(1)).setBorderPosition(ComponentStyle.BORDER_POSITION.INTERNAL).setBorderColor(new Color(206, 206, 206, 255).getRGB());

        entryNameLabel = new GuiLabel(entryName);
        entryNameLabel.getStyleCustomizer().setRelativeWidth(1, RELATIVE_TO_PARENT).setHeight(9);
        entryNameLabel.getStyleCustomizer().setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
        add(entryNameLabel);
    }

    @Override
    public void onFocus() {
        super.onFocus();
        //TODO DEFAULT STYLE IN CSS
        getStyleCustomizer().setBackgroundColor(new Color(0, 0, 0, 0.6f).getRGB());
    }

    @Override
    public void onFocusLoose() {
        super.onFocusLoose();
        getStyleCustomizer().setBackgroundColor(new Color(0, 0, 0, 0.3f).getRGB());
    }
}
