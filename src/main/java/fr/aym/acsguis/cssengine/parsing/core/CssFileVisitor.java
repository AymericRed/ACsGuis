package fr.aym.acsguis.cssengine.parsing.core;

import fr.aym.acsguis.cssengine.parsing.core.objects.CssObject;

public interface CssFileVisitor {
    void onObjectComplete(CssObject currentObject) throws CssException;
}
