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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ACsPlatform
{
    public static final String MOD_ID = "acslib", NAME = "ACsLib", VERSION = "1.0.0", API_VERSION = "1.0";
    private static Logger log = LogManager.getLogger("ACsLib");

    private static final Map<String, ACsService> services = new HashMap<>();

    public static boolean isServiceSupported(String serviceName) {
        return services.containsKey(serviceName);
    }

    public static <T extends ACsService> T provideService(String serviceName) {
        return (T) services.get(serviceName);
    }

    protected static void notifyServices(FMLStateEvent event) {
        services.values().forEach(s -> s.onFMLStateEvent(event));
    }

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
     * Checks the sides where the addon is allowed to run
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

    public static void serviceDebug(ACsService service, String message) {
        log.debug("["+service.getName()+"] "+message);
    }
    public static void serviceInfo(ACsService service, String message) {
        log.info("["+service.getName()+"] "+message);
    }
    public static void serviceError(ACsService service, String message) {
        log.error("["+service.getName()+"] "+message);
    }
    public static void serviceError(ACsService service, String message, Throwable e) {
        log.error("["+service.getName()+"] "+message, e);
    }
    public static void serviceWarn(ACsService service, String message) {
        log.warn("["+service.getName()+"] "+message);
    }
    public static void serviceFatal(ACsService service, String message) {
        log.fatal("["+service.getName()+"] "+message);
    }
    public static void serviceFatal(ACsService service, String message, Throwable e) {
        log.fatal("["+service.getName()+"] "+message, e);
    }
}
