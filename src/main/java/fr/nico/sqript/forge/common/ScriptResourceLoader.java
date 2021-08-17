package fr.nico.sqript.forge.common;

import fr.nico.sqript.ScriptManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.Level;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ScriptResourceLoader implements IResourcePack {
    boolean debug = false;

    @Override
    public InputStream getInputStream(ResourceLocation rl) throws IOException {
        if (!resourceExists(rl)) {
            return null;
        } else {
            File file = new File(new File(Minecraft.getMinecraft().gameDir, "scripts/" + rl.getNamespace()), rl.getPath());

            String realFileName = file.getCanonicalFile().getName();
            if (!realFileName.equals(file.getName())) {
                ScriptManager.log.log(Level.WARN, "[NormalResourceLoader] Resource Location " + rl.toString() + " only matches the file " + realFileName + " because RL is running in an environment that isn't case sensitive in regards to file names. This will not work properly on for example Linux.");
            }

            return new FileInputStream(file);
        }
    }

    @Override
    public boolean resourceExists(ResourceLocation rl) {
        File fileRequested = new File(new File(Minecraft.getMinecraft().gameDir, "scripts/" + rl.getNamespace()), rl.getPath());

        if (debug && !fileRequested.isFile()) {
            ScriptManager.log.log(Level.DEBUG, "[NormalResourceLoader] Asked for resource " + rl.toString() + " but can't find a file at " + fileRequested.getAbsolutePath());
        }

        return fileRequested.isFile();
    }

    @Override
    public Set<String> getResourceDomains() {
        File folder = new File(Minecraft.getMinecraft().gameDir, "scripts");
        if (!folder.exists()) {
            folder.mkdir();
        }
        HashSet<String> folders = new HashSet<String>();

        ScriptManager.log.log(Level.DEBUG, "[SqriptResourceLoader] Resource Loader Domains: ");

        File[] resourceDomains = folder.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);

        for (File resourceFolder : resourceDomains) {
            if (resourceFolder.getName().equals("debug")) {
                debug = true;
            }
        }

        for (File resourceFolder : resourceDomains) {
            ScriptManager.log.log(Level.DEBUG, "[SqriptResourceLoader]  - " + resourceFolder.getName() + " | " + resourceFolder.getAbsolutePath());
            folders.add(resourceFolder.getName());
        }

        return folders;
    }

    @Override
    public IMetadataSection getPackMetadata(MetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    public String getPackName() {
        return "SqriptResources";
    }
}
