package fr.aym.acsguis.test;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.api.ACsGuiFrame;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiButtonWithItem;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.list.GuiDropdownList;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.panel.GuiTabbedPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextField;
import fr.aym.acsguis.utils.GuiCssError;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.List;
import java.util.*;

@ACsGuiFrame
public class GuiDnxDebug extends GuiFrame {
    @ACsGuiFrame.RegisteredStyleSheet
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ACsGuiApi.RES_LOC_ID, "css/dnx_debug.css");

    public GuiDnxDebug() {
        super(new GuiScaler.Identity());
        getStyleCustomizer().setBackgroundColor(Color.TRANSLUCENT);
        setCssClass("home");
        setApplyMcScale(false);
        //DnxCssParser.loadGui(this);
        GuiTabbedPane pane = new GuiTabbedPane();

        setEnableDebugPanel(true);

        GuiPanel general = new GuiPanel();
        general.setCssId("general");
        general.add(new GuiLabel("DynamX debug - general").setCssClass("title"));
        //Options :
        {
            GuiScrollPane pane1 = new GuiScrollPane();
            //Render on/off
            GuiLabel label = new GuiLabel("Rendu du debug : " + (ClientDebugSystem.enableDebugDrawing ? "activé" : "désactivé"));
            pane1.add(label.setCssClass("option-desc"));
            GuiButton b = new GuiButton();
            pane1.add(b.setCssClass("switch-button-" + (ClientDebugSystem.enableDebugDrawing ? "on" : "off")).addClickListener((x, y, button) -> {
                ClientDebugSystem.enableDebugDrawing = !ClientDebugSystem.enableDebugDrawing;
                b.setCssClass("switch-button-" + (ClientDebugSystem.enableDebugDrawing ? "on" : "off"));
                label.setText("Rendu du debug : " + (ClientDebugSystem.enableDebugDrawing ? "activé" : "désactivé"));
            }));
            b.getStyleCustomizer().setYPos(0);
            label.getStyleCustomizer().setYPos(0);

            //Profiling
            boolean active = EnumTerrainDebugOptions.PROFILING.isActive(ClientDebugSystem.terrainDebugMode);
            GuiLabel label1 = new GuiLabel("Profiling : " + (active ? "activé" : "désactivé"));
            pane1.add(label1.setCssClass("option-desc"));

            GuiPanel property = new GuiPanel();
            property.add(new GuiLabel("Test config horizontale")).add(new GuiTextField().setText("zougoulou")).setCssClass("property_horizontal");
            pane1.add(property);

            pane1.add(new GuiPanel().add(new GuiLabel("Test config verticale")).add(new GuiTextField().setText("ngolokante")).setCssClass("property_vertical"));

            GuiButton b1 = new GuiButton();
            pane1.add(b1.setCssClass("switch-button-" + (active ? "on" : "off")).addClickListener((mx, my, button) -> {
                if (EnumTerrainDebugOptions.PROFILING.isActive(ClientDebugSystem.terrainDebugMode))
                    ClientDebugSystem.terrainDebugMode = EnumTerrainDebugOptions.PROFILING.removeDebugMode(ClientDebugSystem.terrainDebugMode);
                else
                    ClientDebugSystem.terrainDebugMode = EnumTerrainDebugOptions.PROFILING.applyDebugMode(ClientDebugSystem.terrainDebugMode);
                boolean nactive = EnumTerrainDebugOptions.PROFILING.isActive(ClientDebugSystem.terrainDebugMode);
                b1.setCssClass("switch-button-" + (nactive ? "on" : "off"));
                label1.setText("Profiling : " + (nactive ? "activé" : "désactivé"));
            }));
            b1.getStyleCustomizer().setYPos(25);
            label1.getStyleCustomizer().setYPos(25);

            GuiLabel box = new GuiLabel("Recharger les packs");
            box.setCssId("reload_packs").setCssClass("reload_button");
            box.addClickListener((x, y, bu) -> {
                box.setEnabled(false);
                box.setText("Indisponible ici...");
            });
            pane1.add(box);
            GuiLabel box2 = new GuiLabel("Recharger les modèles");
            box2.setCssId("reload_models").setCssClass("reload_button");
            box2.addClickListener((x, y, bu) -> {
                //mc.debugFeedbackTranslated("debug.reload_resourcepacks.message");
                box2.setEnabled(false);
                box.setText("Indisponible ici...");
            });
            pane1.add(box2);
            GuiLabel box3 = new GuiLabel("Recharger les styles css");
            box3.setCssId("reload_css").setCssClass("reload_button");
            box3.addClickListener((x, y, bu) -> {
                box3.setEnabled(false);
                box3.setText("Rechargement en cours...");
                ACsGuiApi.reloadCssStyles(this);
                box3.setEnabled(true);
                if (ACsGuiApi.getErrorTracker().hasErrors(ACsGuiApi.getCssErrorType()))
                    box3.setText(TextFormatting.RED + "Des styles css ont des erreurs");
                else
                    box3.setText("Styles css rechargés");
            });
            pane1.add(box3);
            GuiLabel box4 = new GuiLabel("Recharger tout");
            box4.setCssId("reload_all").setCssClass("reload_button");
            box4.addClickListener((x, y, bu) -> {
                box4.setEnabled(false);
                box4.setText("Rechargement en cours...");
                ACsGuiApi.reloadCssStyles(this);
                box4.setEnabled(true);
                if (ACsGuiApi.getErrorTracker().hasErrors(ACsGuiApi.getCssErrorType()))
                    box4.setText(TextFormatting.RED + "Des styles css ont des erreurs");
                else
                    box4.setText("Styles css rechargés");
            });
            pane1.add(box4);

            /*GuiButtonWithItem icon = new GuiButtonWithItem(new ItemStack(Blocks.GLASS_PANE));
            icon.setCssId("acacia.icon");
            pane1.add(icon);*/

            general.add(pane1);
        }
        pane.addTab("General", general);

        general = new GuiPanel();
        general.setCssId("terrain");
        general.add(new GuiLabel("DynamX debug - terrain").setCssClass("title"));
        int y = 0;
        {
            Map<EnumTerrainDebugOptions, GuiButton> terrainButtons = new HashMap<>();
            Map<EnumTerrainDebugOptions, GuiLabel> terrainLabels = new HashMap<>();
            GuiScrollPane pane1 = new GuiScrollPane();
            for (EnumTerrainDebugOptions option : EnumTerrainDebugOptions.values()) {
                if (!option.showOnDebugOptions)
                    continue;
                boolean active = option.isActive(ClientDebugSystem.terrainDebugMode);
                GuiLabel label1 = new GuiLabel("Debug " + option.name() + " : " + (active ? "activé" : "désactivé"));
                terrainLabels.put(option, label1);
                pane1.add(label1.setCssClass("option-desc"));

                GuiButton b1 = new GuiButton();
                terrainButtons.put(option, b1);
                pane1.add(b1.setCssClass("switch-button-" + (active ? "on" : "off")).addClickListener((mx, my, button) -> {
                    if (option.isActive(ClientDebugSystem.terrainDebugMode))
                        ClientDebugSystem.terrainDebugMode = option.removeDebugMode(ClientDebugSystem.terrainDebugMode);
                    else
                        ClientDebugSystem.terrainDebugMode = option.applyDebugMode(ClientDebugSystem.terrainDebugMode);
                    for (EnumTerrainDebugOptions noption : EnumTerrainDebugOptions.values()) {
                        if (!noption.showOnDebugOptions)
                            continue;
                        boolean nactive = noption.isActive(ClientDebugSystem.terrainDebugMode);
                        terrainButtons.get(noption).setCssClass("switch-button-" + (nactive ? "on" : "off"));
                        terrainLabels.get(noption).setText("Debug " + noption.name() + " : " + (nactive ? "activé" : "désactivé"));
                    }
                }));

                b1.getStyleCustomizer().setYPos(y);
                label1.getStyleCustomizer().setYPos(y);
                y += 25;
            }
            general.add(pane1);
        }
        pane.addTab("Terrain", general);

        general = new GuiPanel();
        general.setLayout(new GridLayout(-1, 20, 0, GridLayout.GridDirection.HORIZONTAL, 1));
        general.add(new GuiLabel("DynamX debug - vehicles").setCssClass("title"));
        general.setCssId("vehicles");
        y = 0;
        {
            Map<EnumVehicleDebugOptions, GuiButton> vehicleButtons = new HashMap<>();
            Map<EnumVehicleDebugOptions, GuiLabel> vehicleLabels = new HashMap<>();
            GuiScrollPane pane1 = new GuiScrollPane();
            pane1.setCssId("lolt");
            for (EnumVehicleDebugOptions option : EnumVehicleDebugOptions.values()) {
                if (option == EnumVehicleDebugOptions.NONE)
                    continue;
                boolean active = option.isActive(ClientDebugSystem.entityDebugMode);
                GuiLabel label1 = new GuiLabel("Debug " + option.name() + " : " + (active ? "activé" : "désactivé"));
                vehicleLabels.put(option, label1);
                pane1.add(label1.setCssClass("option-desc"));

                GuiButton b1 = new GuiButton();
                vehicleButtons.put(option, b1);
                pane1.add(b1.setCssClass("switch-button-" + (active ? "on" : "off")).addClickListener((mx, my, button) -> {
                    if (option.isActive(ClientDebugSystem.entityDebugMode))
                        ClientDebugSystem.entityDebugMode = option.removeDebugMode(ClientDebugSystem.entityDebugMode);
                    else
                        ClientDebugSystem.entityDebugMode = option.applyDebugMode(ClientDebugSystem.entityDebugMode);
                    for (EnumVehicleDebugOptions noption : EnumVehicleDebugOptions.values()) {
                        if (noption == EnumVehicleDebugOptions.NONE)
                            continue;
                        boolean nactive = noption.isActive(ClientDebugSystem.entityDebugMode);
                        vehicleButtons.get(noption).setCssClass("switch-button-" + (nactive ? "on" : "off"));
                        vehicleLabels.get(noption).setText("Debug " + noption.name() + " : " + (nactive ? "activé" : "désactivé"));
                    }
                }));

                b1.getStyleCustomizer().setYPos(y);
                label1.getStyleCustomizer().setYPos(y);
                y += 25;
            }
            general.add(pane1);
        }
        pane.addTab("Vehicles", general);

        general = new GuiPanel();
        general.setCssId("loadinglog");
        pane.addTab(TextFormatting.GOLD + "Erreurs", general);
        general.setLayout(new GridLayout(-1, 20, 0, GridLayout.GridDirection.HORIZONTAL, 1));
        general.add(new GuiDropdownList("Test dropdown", Arrays.asList("t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10")).setChangeCallback((s) -> System.out.println("Tu as cliqué sur " + s)));
        general.add(new GuiLabel("ptdrrrrrr"));
        general.add(new GuiLabel("LOL"));
        general.add(new GuiLabel("the end"));
        general.add(new GuiLabel("test2"));
        general.add(new GuiLabel("test3"));
        general.add(new GuiLabel("test4"));
        general.add(new GuiLabel("test5"));
        general.add(new GuiLabel("test6"));
        general.add(new GuiLabel("test7"));
        general.add(new GuiLabel("test8"));
        general.add(new GuiLabel("test9"));
        general.add(new GuiLabel("test10"));
        /*pane.getTabButton(3).addClickListener((mx, my, button) -> {
            if(button == 0)
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiLoadingErrors().getGuiScreen()));
        });*/

        general = new GuiPanel();
        general.setCssId("showcsslog");
        pane.addTab("Log CSS", general);
        pane.getTabButton(4).addClickListener((mx, my, button) -> {
            if (button == 0)
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiCssError().getGuiScreen()));
        });

        pane.selectTab(0);
        add(pane);


        add(new GuiLabel("Entièrement designé en CSS").setCssId("credits"));

        setCssId("lol");
        setCssCode("color: red; background-color: orange;");
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.singletonList(RESOURCE_LOCATION);
    }

    @Override
    public boolean doesPauseGame() {
        return false;
    }

    @Override
    public boolean needsCssReload() {
        return true;
    }
}
