package fr.aym.acslib.services;

import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An ACsLib service to load <br>
 *     All classes having this annotation must implement {@link ACsService}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ACsRegisteredService
{
    /**
     * @return The name of the service (should be unique per service-type, but two services with the same name can be loaded, and the newer (higher version) will be kept)
     */
    String name();

    /**
     * @return The version of the service (used for service compatibility)
     */
    String version();

    /**
     * @return The sides where the service should be loaded, client and server by default
     */
    Side[] sides() default {Side.CLIENT, Side.SERVER};
}
