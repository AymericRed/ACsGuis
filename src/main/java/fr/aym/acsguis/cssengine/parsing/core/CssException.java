package fr.aym.acsguis.cssengine.parsing.core;

public class CssException extends Exception
{
    public CssException(String message) {
        super(message);
    }

    public CssException(String message, Throwable cause) {
        super(message, cause);
    }
}
