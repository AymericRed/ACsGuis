package fr.aym.acslib.impl.error_tracking;

import fr.aym.acslib.services.ACsRegisteredService;
import fr.aym.acslib.services.error_tracking.ErrorTrackingService;
import fr.aym.acslib.services.error_tracking.TrackedErrorLevel;
import fr.aym.acslib.services.error_tracking.TrackedErrorType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to keep trace of all loading errors, and show them in-game
 * TODO UPDATE DOC
 */
@ACsRegisteredService(name = "errtrack", version = "1.0.0")
public class ACsLibErrorTracker implements ErrorTrackingService
{
    private final Map<String, LocatedErrorList> errors = new HashMap<>();

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public void initService() {}

    /**
     * Reports an error
     *
     * @param type Where it happened
     * @param location The location/addon that was loading (or the part of DynamX)
     * @param title The title of the error
     * @param message A detailed message
     * @param level The level of the error
     */
    @Override
    public void addError(TrackedErrorType type, String location, String title, String message, TrackedErrorLevel level)
    {
        if(!errors.containsKey(location))
            errors.put(location, new LocatedErrorListImpl());
        errors.get(location).addError(type, title, message, level);
    }

    /**
     * Clears all errors of the given type (useful for packs or models reload)
     */
    @Override
    public void clear(TrackedErrorType type)
    {
        errors.values().forEach(p -> p.clear(type));
    }

    @Override
    public void clear() {
        errors.clear();
    }

    /**
     * @return True if there are errors of the given type
     */
    @Override
    public boolean hasErrors(TrackedErrorType... types) {
        for(TrackedErrorType type : types)
        {
            for(LocatedErrorList list : errors.values())
            {
                if(!list.getErrors(type).isEmpty())
                    return true;
            }
        }
        return false;
    }

    private final Map<ResourceLocation, TrackedErrorType> errorTypeMap = new HashMap<>();

    @Override
    public TrackedErrorType createErrorType(ResourceLocation id, String label) {
        TrackedErrorType t;
        errorTypeMap.put(id, t = new TrackedErrorType(id, label));
        return t;
    }

    @Override
    public TrackedErrorType findErrorType(ResourceLocation id) {
        return errorTypeMap.get(id);
    }

    /**
     * Self-explaining
     */
    public Map<String, LocatedErrorList> getAllErrors()
    {
        return errors;
    }

    public static class ErrorData implements TrackedError
    {
        private final TrackedErrorType type;
        private final String title, desc;
        private final TrackedErrorLevel level;

        public ErrorData(TrackedErrorType type, String title, String desc, TrackedErrorLevel level) {
            this.type = type;
            this.title = title;
            this.desc = desc;
            this.level = level;
        }

        @Override
        public TrackedErrorLevel getLevel() {
            return level;
        }

        @Override
        public TrackedErrorType getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return level.color+" -> "+title+" \n "+"   Details : "+desc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ErrorData errorData = (ErrorData) o;
            return type == errorData.type &&
                    title.equals(errorData.title) &&
                    desc.equals(errorData.desc) &&
                    level == errorData.level;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, title, desc, level);
        }
    }

    /**
     * Groups all errors of one pack
     */
    public static class LocatedErrorListImpl implements LocatedErrorList
    {
        private final List<TrackedError> errors = new ArrayList<>();
        private TrackedErrorLevel highestLevel = TrackedErrorLevel.ADVICE;

        public void addError(TrackedErrorType type, String title, String message, TrackedErrorLevel level)
        {
            ErrorData d = new ErrorData(type, title, message, level);
            if(!errors.contains(d))
                errors.add(d);
            if(level.ordinal() > highestLevel.ordinal())
                highestLevel = level;
        }

        @Override
        public List<TrackedError> getErrors() {
            return errors;
        }

        public List<TrackedError> getErrors(TrackedErrorLevel level) {
            return errors.stream().filter(e -> e.getLevel() == level).collect(Collectors.toList());
        }

        public TrackedErrorLevel getHighestLevel() {
            return highestLevel;
        }

        /**
         * @return Color of the highest error level in this pack
         */
        public TextFormatting getColor() {
            return getHighestLevel().color;
        }

        /**
         * @return All errors of the given type
         */
        public List<TrackedError> getErrors(TrackedErrorType type) {
            return errors.stream().filter(e -> e.getType() == type).collect(Collectors.toList());
        }

        /**
         * Removes errors of the given type
         */
        public void clear(TrackedErrorType type) {
            highestLevel = TrackedErrorLevel.ADVICE;
            List<TrackedError> remove = new ArrayList<>();
            for(TrackedError d : errors)
            {
                if(d.getType() == type)
                    remove.add(d);
                else if(d.getLevel().ordinal() > highestLevel.ordinal())
                    highestLevel = d.getLevel();
            }
            errors.removeAll(remove);
        }
    }
}
