package fr.aym.acslib;

import fr.aym.acslib.services.ACsRegisteredService;
import fr.aym.acslib.services.ACsService;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  Main ACsLib class <br>
 *  Provides a stable services system, to load and use main functions of my mods without version conflicts <br>
 *     Existing services : <br>
 *          <li>ThrLoad : a threaded loading system for your mods <br></li>
 *          <li>errtrack : an error tracking service to easily report and show your error to the users <br></li>
 *          <li>acsguis : the ACsGuis api loader <br></li>
 *          <li>MPS : a protected-resources (ciphered and/or remotely hosted) loader, implemented in DynamX <br></li>
 *          <li>dnx_stats : an error and system info reporting service, implemented in DynamX</li>
 *
 * @see fr.aym.acslib.services.thrload.ThreadedLoadingService
 * @see fr.aym.acslib.services.error_tracking.ErrorTrackingService
 * @see fr.aym.acsguis.api.ACsGuiApi
 */
public class ACsPlatform
{
    public static final String MOD_ID = "acslib", NAME = "ACsLib", VERSION = "1.0.0";
    private static final Logger log = LogManager.getLogger("ACsLib");

    /**
     * Loaded services
     */
    private static final Map<String, ACsService> services = new HashMap<>();

    /**
     * @return True if this service is loaded
     */
    public static boolean isServiceSupported(String serviceName) {
        return services.containsKey(serviceName);
    }

    /**
     * @param <T> The service base interface, to avoid a manual cast
     * @return The requested service if loaded, or null
     */
    public static <T extends ACsService> T provideService(String serviceName) {
        return (T) services.get(serviceName);
    }

    /**
     * Sends this event to all services
     */
    protected static void notifyServices(FMLStateEvent event) {
        services.values().forEach(s -> s.onFMLStateEvent(event));
    }

    /**
     * Locates and loads all services
     *
     * @see ACsRegisteredService
     */
    protected static void locateServices(FMLConstructionEvent event)
    {
        Set<ASMDataTable.ASMData> modData = event.getASMHarvestedData().getAll(ACsRegisteredService.class.getName());

        for(ASMDataTable.ASMData data : modData)
        {
            if(canRunOn(data.getAnnotationInfo().get("sides"), event.getSide())) {
                String name = data.getClassName();
                try {
                    Class<? extends ACsService> addon = (Class<? extends ACsService>) Class.forName(data.getClassName());
                    ACsRegisteredService an = addon.getAnnotation(ACsRegisteredService.class);
                    name = an.name();
                    log.debug("Found service candidate " + an.name() + " in mod " + data.getCandidate().getModContainer().getName() +". In version "+an.version());
                    if(services.containsKey(an.name()))
                    {
                        ComparableVersion v1 = new ComparableVersion(services.get(an.name()).getVersion());
                        ComparableVersion v2 = new ComparableVersion(an.version());
                        if(v2.compareTo(v1) > 0) {

                            log.info("Service "+an.name()+" is already registered, ignoring version "+services.get(an.name()).getVersion()+" and keeping the newer "+an.version());
                            services.put(an.name(), addon.newInstance());
                        }
                        else
                            log.info("Service "+an.name()+" is already registered, ignoring version "+an.version()+" and keeping the newer "+services.get(an.name()).getVersion());
                    }
                    else
                        services.put(an.name(), addon.newInstance());
                } catch (Exception e) {
                    log.error("Service " + name + " cannot be loaded !", e);
                }
            }
        }
        services.values().forEach(s -> {
            log.info("Registered "+s.getName()+" service V."+s.getVersion());
            s.initService();
        });
    }

    /**
     * Checks the sides where the service is allowed to run
     *
     * @param addonSides annotation data
     */
    private static boolean canRunOn(Object addonSides, Side current) {
        if(addonSides == null)
            return true; //default behavior
        for (ModAnnotation.EnumHolder s : (Iterable<ModAnnotation.EnumHolder>) addonSides) {
            if(s.getValue().equalsIgnoreCase(current.name()))
                return true;
        }
        return false;
    }

    /**
     * Logs a debug message
     */
    public static void serviceDebug(ACsService service, String message) {
        log.debug("["+service.getName()+"] "+message);
    }
    /**
     * Logs an info message
     */
    public static void serviceInfo(ACsService service, String message) {
        log.info("["+service.getName()+"] "+message);
    }
    /**
     * Logs a warning message
     */
    public static void serviceWarn(ACsService service, String message) {
        log.warn("["+service.getName()+"] "+message);
    }
    /**
     * Logs an error message
     */
    public static void serviceError(ACsService service, String message) {
        log.error("["+service.getName()+"] "+message);
    }
    /**
     * Logs an error message and prints the error
     */
    public static void serviceError(ACsService service, String message, Throwable e) {
        log.error("["+service.getName()+"] "+message, e);
    }
    /**
     * Logs a fatal message
     */
    public static void serviceFatal(ACsService service, String message) {
        log.fatal("["+service.getName()+"] "+message);
    }
    /**
     * Logs a fatal message and prints the error
     */
    public static void serviceFatal(ACsService service, String message, Throwable e) {
        log.fatal("["+service.getName()+"] "+message, e);
    }
}
