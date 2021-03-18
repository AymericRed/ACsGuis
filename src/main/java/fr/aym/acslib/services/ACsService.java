package fr.aym.acslib.services;

import net.minecraftforge.fml.common.event.FMLStateEvent;

public interface ACsService
{
    String getName();
    String getVersion();

    void initService();
    default void onFMLStateEvent(FMLStateEvent event) {}
}