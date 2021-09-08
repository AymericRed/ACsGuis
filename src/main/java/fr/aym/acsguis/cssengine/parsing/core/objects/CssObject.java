package fr.aym.acsguis.cssengine.parsing.core.objects;

import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract css object with selectors and properties
 */
public interface CssObject
{
    /**
     * @return All properties of this object
     */
    List<CssProperty> getAllDeclarations();

    /**
     * @return The line where the object is declared
     */
    String getSourceLocation();

    /**
     * @return The selectors of this object
     */
    Iterable<? extends CompoundCssSelector> getSelectors();

    /**
     * A basic css object with selectors and properties
     */
    class BasicCssObject implements CssObject
    {
        private final String sourceLocation;
        private final List<CompoundCssSelector> selectors;
        private final List<CssProperty> declarations = new ArrayList<>();

        public BasicCssObject(String sourceLocation, List<CompoundCssSelector> selectors) {
            this.sourceLocation = sourceLocation;
            this.selectors = selectors;
        }

        @Override
        public List<CssProperty> getAllDeclarations() {
            return declarations;
        }

        @Override
        public String getSourceLocation() {
            return sourceLocation;
        }

        @Override
        public Iterable<? extends CompoundCssSelector> getSelectors() {
            return selectors;
        }
    }

    /**
     * An annotation object, with properties but no selector (except the annotation)
     */
    class AnnotationObject implements CssObject
    {
        private final String sourceLocation;
        private final CssAnnotation annotationIn;
        private final List<CssProperty> declarations = new ArrayList<>();

        public AnnotationObject(String sourceLocation, CssAnnotation annotationIn) {
            this.sourceLocation = sourceLocation;
            this.annotationIn = annotationIn;
        }

        public CssAnnotation getAnnotation() {
            return annotationIn;
        }

        @Override
        public List<CssProperty> getAllDeclarations() {
            return declarations;
        }

        @Override
        public String getSourceLocation() {
            return sourceLocation;
        }

        @Override
        public Iterable<? extends CompoundCssSelector> getSelectors() {
            return null;
        }
    }
}
