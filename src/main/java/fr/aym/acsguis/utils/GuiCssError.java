package fr.aym.acsguis.utils;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextArea;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Automatically shown when failing to show a gui
 */
public class GuiCssError extends GuiFrame {
    /**
     * Displays the given error
     *
     * @param guiName Name of the faulty gui
     * @param e       The error
     */
    public GuiCssError(String guiName, Throwable e) {
        super(new GuiScaler.Identity());
        ScaledResolution r = new ScaledResolution(mc);
        getStyleCustomizer().setBackgroundColor(Color.TRANSLUCENT)
                .setPosition(10, 10).setSize(r.getScaledWidth() - 20, r.getScaledHeight() - 20);
        addResizeListener((w, h) -> {
            getStyleCustomizer().setSize(w - 20, h - 20);
        });

        GuiLabel error = new GuiLabel();
        error.getStyleCustomizer().setSize(r.getScaledWidth() - 20, r.getScaledHeight() - 20);
        error.setMaxTextLength(Integer.MAX_VALUE);
        error.addResizeListener((w, h) -> {
            error.getStyleCustomizer().setSize(w - 20, h - 20);
        });
        error.setText("Cannot display gui " + guiName + " " + "\n " + "\n" + "CSS error " + e.toString());
        while (e.getCause() != null) {
            e = e.getCause();
            error.setText(error.getText() + " " + "\n " + "\n" + " \t Caused by : " + e.toString());
        }
        add(error.getStyleCustomizer().setForegroundColor(0xFFAAAA).setBackgroundColor(Integer.MIN_VALUE).getOwner());
    }

    private GuiPanel summary;
    private GuiComponent displayed;

    /**
     * Displays all parsed data
     */
    public GuiCssError() {
        super(new GuiScaler.Identity());
        ScaledResolution r = new ScaledResolution(mc);
        getStyleCustomizer().setBackgroundColor(Color.TRANSLUCENT)
                .setPosition(10, 10).setSize(r.getScaledWidth() - 20, r.getScaledHeight() - 20);
        addResizeListener((w, h) -> {
            getStyleCustomizer().setSize(w - 20, h - 20);
        });

        int width = r.getScaledWidth() - 20;
        summary = new GuiScrollPane();
        summary.getStyleCustomizer().setSize(width, r.getScaledHeight() - 20);

        summary.add(new GuiLabel("Click on any css sheet to view it, then press escape to go back").getStyleCustomizer().setPaddingLeft(2).setPaddingTop(2).setWidth(width).getOwner());
        int i = 1;
        for (Map.Entry<ResourceLocation, Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>>> entry : ACsGuisCssParser.getCssStyleSheets().entrySet()) {
            ResourceLocation res = entry.getKey();
            Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> m = entry.getValue();
            summary.add(new GuiLabel("+ Style sheet : " + res).getStyleCustomizer().setPaddingLeft(2).setPaddingTop(2).setYPos(i * 22).setWidth(width).getOwner().addClickListener((x, y, b) -> {
                remove(summary);

                GuiTextArea error = new GuiTextArea();
                error.setMaxTextLength(Integer.MAX_VALUE);
                error.getStyleCustomizer().setPaddingTop(4).setPaddingLeft(4).setSize(r.getScaledWidth() - 20, r.getScaledHeight() - 20);
                error.addResizeListener((w, h) -> {
                    error.getStyleCustomizer().setSize(w - 20, h - 20);
                });

                final StringBuilder text = new StringBuilder("Loaded CSS data :" + "\n" + " \n");
                text.append("Style sheet : " + res.toString() + "\n");
                m.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
                    text.append("====> Selector : " + TextFormatting.GOLD).append(e.getKey());
                    text.append(TextFormatting.RESET + " has ");
                    e.getValue().forEach((st, prop) -> {
                        text.append("\n").append("\t -->" + TextFormatting.DARK_AQUA).append(st.key).append(TextFormatting.RESET + " = " + TextFormatting.AQUA);
                        if (prop.getType().isNormal())
                            text.append(prop.getValue());
                        else
                            text.append(prop.getType().toString().toLowerCase());
                    });
                    text.append(TextFormatting.RESET + "\n");
                });
                text.append(" \n \n");

                displayed = error;
                error.setFocused(true);
                error.setText(text.toString());
                add(error.getStyleCustomizer().setForegroundColor(0x88FF88).setBackgroundColor(Integer.MIN_VALUE).getOwner());
            }));
            i++;
        }

        summary.setFocused(true);
        add(summary.getStyleCustomizer().setForegroundColor(0x88FF88).setBackgroundColor(Integer.MIN_VALUE).getOwner());
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 && displayed != null) {
            remove(displayed);
            add(summary);
            summary.setFocused(true);
            displayed = null;
        } else
            super.onKeyTyped(typedChar, keyCode);
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean usesDefaultStyle() {
        return false;
    }
}
