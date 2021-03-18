package fr.aym.acslib.services;

import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ACsRegisteredService
{
    /**
     * @return The name of the addon (used for logging)
     */
    String name();

    /**
     * @return The version of the addon (used for addon compatibility)
     */
    String version();

    /**
     * @return The sides where the addon should be loaded, client and server by default
     */
    Side[] sides() default {Side.CLIENT, Side.SERVER};
}
