package fr.aym.acslib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

/**
 * You can call it AymLib
 *
 * @see ACsPlatform
 */
@Mod(modid = ACsPlatform.MOD_ID, name = ACsPlatform.NAME, version = ACsPlatform.VERSION, dependencies = "required-before:sqript@${version}")
public class ACsLibMod
{
    @Mod.Instance(value = ACsPlatform.MOD_ID)
    public static ACsLibMod instance;

    @Mod.EventHandler()
    public void construct(FMLConstructionEvent event) {
        ACsPlatform.locateServices(event);
        ACsPlatform.notifyServices(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ACsPlatform.notifyServices(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ACsPlatform.notifyServices(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ACsPlatform.notifyServices(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        ACsPlatform.notifyServices(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        ACsPlatform.notifyServices(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        ACsPlatform.notifyServices(event);
    }
}
