package fr.aym.acsguis.cssengine.parsing.core;

import fr.aym.acsguis.cssengine.parsing.core.objects.CssObject;

/**
 * Visits a css files to gather its objects
 */
public interface CssFileVisitor {
    /**
     * Fired when an object is completed
     *
     * @param currentObject The completed object
     * @throws CssException If an error occurs while handling this object
     */
    void onObjectComplete(CssObject currentObject) throws CssException;
}
