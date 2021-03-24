package fr.aym.acslib.services.error_tracking;

import fr.aym.acslib.services.ACsService;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.Map;

//TODO DOC
public interface ErrorTrackingService extends ACsService
{
    TrackedErrorType createErrorType(ResourceLocation id, String label);

    TrackedErrorType findErrorType(ResourceLocation id);

    /**
     * Reports an error
     *
     * @param type Where it happened
     * @param location The pack/addon that was loading (or the part of DynamX)
     * @param title The title of the error
     * @param exception The exception, to show it's message
     * @param level The level of the error
     */
    default void addError(TrackedErrorType type, String location, String title, Exception exception, TrackedErrorLevel level)
    {
        addError(type, location, title, "Error : "+exception.toString(), level);
    }
    /**
     * Reports an error
     *
     * @param type Where it happened
     * @param location The pack/addon that was loading (or the part of DynamX)
     * @param title The title of the error
     * @param message A detailed message
     * @param level The level of the error
     */
    void addError(TrackedErrorType type, String location, String title, String message, TrackedErrorLevel level);

    void clear();

    /**
     * Clears all errors of the given type (useful for packs or models reload)
     */
    void clear(TrackedErrorType type);

    /**
     * @return True if there are errors of the given type
     */
    boolean hasErrors(TrackedErrorType... types);

    @Override
    default String getName() {
        return "errtrack";
    }

    Map<String, LocatedErrorList> getAllErrors();

    interface LocatedErrorList
    {
        Collection<TrackedError> getErrors();

        Collection<TrackedError> getErrors(TrackedErrorType type);

        Collection<TrackedError> getErrors(TrackedErrorLevel fatal);

        void addError(TrackedErrorType type, String title, String message, TrackedErrorLevel level);

        void clear(TrackedErrorType type);

        TextFormatting getColor();
    }

    interface TrackedError
    {
        TrackedErrorLevel getLevel();
        TrackedErrorType getType();
    }
}
