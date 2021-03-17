package fr.aym.acsguis.api;

public class ACsGuisErrorTracker //TODO DOC
{
    private static ErrorTracker tracker;

    public static void addError(String title, String message, ErrorLevel level) {
        if(tracker != null)
            tracker.addError(title, message, level);
    }

    public static void addError(String title, String message, Exception e, ErrorLevel level) {
        if(tracker != null)
            tracker.addError(title, message, e, level);
    }

    public static void clear() {
        if(tracker != null)
            tracker.clearErrors();
    }

    public static boolean hasErrors() {
        return tracker != null && tracker.hasErrors();
    }

    public static void setErrorTracker(ErrorTracker tracker) {
        ACsGuisErrorTracker.tracker = tracker;
    }

    public enum ErrorLevel
    {
        ADVICE, LOW, HIGH, FATAL
    }

    public interface ErrorTracker
    {
        void addError(String title, String message, ErrorLevel level);
        void addError(String title, String message, Exception e, ErrorLevel level);
        void clearErrors();
        boolean hasErrors();
    }
}
