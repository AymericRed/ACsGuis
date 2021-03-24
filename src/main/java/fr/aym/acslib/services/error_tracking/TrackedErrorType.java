package fr.aym.acslib.services.error_tracking;

import fr.aym.acslib.ACsPlatform;
import net.minecraft.util.ResourceLocation;

public class TrackedErrorType
{
    public static final TrackedErrorType ACSLIBERROR = new TrackedErrorType(new ResourceLocation(ACsPlatform.MOD_ID, "acsliberror"), "ACsLib error");

    private final ResourceLocation id;
    private final String label;

    public TrackedErrorType(ResourceLocation id, String label) {
        this.id = id;
        this.label = label;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
