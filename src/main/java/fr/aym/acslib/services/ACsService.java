package fr.aym.acslib.services;

import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * An {@link fr.aym.acslib.ACsPlatform} service <br>
 *     Should have the {@link ACsRegisteredService} annotation to be loaded
 */
public interface ACsService
{
    /**
     * @return The name of the service (should be unique per service-type, but two services with the same name can be loaded, and the newer (higher version) will be kept)
     */
    String getName();
    /**
     * @return The version of the service (used for service compatibility)
     */
    String getVersion();

    /**
     * Fired on service initialization, after duplicates conflicts are solved
     */
    void initService();

    /**
     * Notifies of an fml loading event
     */
    default void onFMLStateEvent(FMLStateEvent event) {}
}