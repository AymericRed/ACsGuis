package fr.aym.acsguis.cssengine.parsing.core;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.cssengine.parsing.core.objects.*;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.selectors.CssSelector;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CssFileReader { //TODO OMG SO MUCH DOC
    private static final List<CssAnnotation> annotations = Arrays.asList(new CssAnnotation("@font-face"));

    static CssObject currentObject = null;
    static boolean hadBeginObject = false;
    static boolean inComment = false;

    public static void readCssFile(String sheetName, InputStream inputStream, CssFileVisitor visitor) throws CssException {
        //System.out.println("===========");
        //System.out.println("Parsing "+sheetName);
        Scanner sc = new Scanner(inputStream);
        int lineNumber = 0;
        currentObject = null;
        hadBeginObject = false;
        inComment = false;
        while (sc.hasNextLine()) {
            lineNumber++;
            try {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    //System.out.println("Parsing line "+lineNumber+" : "+line);
                    if (inComment && line.contains("*/")) {
                        line = line.substring(line.indexOf("*/") + 2).trim();
                        inComment = false;
                    }
                    //TODO WORKING MULTIPLE PROPERTIES ON ONE LINE
                    if (!inComment && !line.isEmpty()) {
                        while (line.contains("/*")) {
                            if (line.contains("*/")) { //Keep start and end
                                line = line.substring(0, line.indexOf("/*")) + line.substring(line.indexOf("*/") + 2);
                                inComment = false;
                                //System.out.println("Sliced to "+line);
                            } else {
                                line = line.substring(0, line.indexOf("/*"));
                                inComment = true;
                                //System.out.println("Keeping "+line);
                            }
                        }
                        if(line.isEmpty()) {
                            //System.out.println("Empty");
                            continue;
                        }
                        if (line.contains("*/")) {
                            throw new CssException("Illegal end of comment at line "+lineNumber+" of "+sheetName);
                        }
                        if (currentObject == null) {
                            if(line.contains("{")) { //EMPECHE LE TOUT EN UNE LIGNE
                                hadBeginObject = true;
                                currentObject = findObject("Line "+lineNumber+" of "+sheetName, line.substring(0, line.indexOf("{")).trim());
                            } else {
                                currentObject = findObject("Line " + lineNumber + " of " + sheetName, line.trim());
                                hadBeginObject = false;
                            }
                            //System.out.println("Have set current to "+currentObject);
                            continue; //TEMP
                        } else if (!hadBeginObject) {
                            if (!line.contains("{")) {
                                throw new CssException("Found a property at line "+lineNumber+" of "+sheetName+" but current selector is not opened with a '{'");
                            }
                            hadBeginObject = true;
                            //System.out.println("Having beginned");
                            continue; //TEMP
                        } else if (line.contains("{")) {
                            throw new CssException("Found an unexpected start of block '{' at line "+line+" of "+sheetName);
                        }
                        if (line.contains("}")) {
                            visitor.onObjectComplete(currentObject);
                            currentObject = null;
                            //System.out.println("Has ended");
                            continue; //TEMP
                        }
                        //line = line.replaceAll(" ", "");
                        //System.out.println("SO WE PARSE "+line);
                        line = line.substring(line.indexOf("{") + 1).trim();
                        if (line.contains(";")) {
                            String[] props = line.split(";");
                            for (String prop : props) {
                                if (!prop.trim().isEmpty()) {
                                    currentObject.getAllDeclarations().add(parseProperty("Line " + lineNumber + " of " + sheetName, prop));
                                }
                            }
                        } else {
                            currentObject.getAllDeclarations().add(parseProperty("Line "+lineNumber+" of "+sheetName, line));
                        }
                    }
                }
            } catch (CssException e) {
                throw e;
            } catch (Exception e) {
                throw new CssException("Error while parsing line "+lineNumber+" of "+sheetName, e);
            }
        }
        if(inComment) {
            throw new CssException("Finished css file "+sheetName+" in a comment !");
        }
    }

    public static CssProperty parseProperty(String location, String line) throws CssException {
        //System.out.println("Parsing " + line);
        if (line.contains(":")) {
            String[] data = line.split(":", 2);
            /*if (!currentObject.isKeySupported(data[0])) {
                throw new CssException();
            }*/
            String value = data[1].trim();
            CssValue cssValue;
            if (!value.contains(" ") && (value.equals("0") || value.endsWith("px"))) {
                cssValue = new CssIntValue(Integer.parseInt(value.replace("px", "")));
            } else if (!value.contains(" ") && value.endsWith("%")) {
                cssValue = new CssRelativeValue(Integer.parseInt(value.replace("%", "")));
            } else {
                if(value.startsWith("\""))
                    value = value.substring(1);
                if(value.endsWith("\""))
                    value = value.substring(0, value.length()-1);
                //System.out.println("SET VALUE OF "+data[0]+" to "+value);
                cssValue = new CssStringValue(value);
            }
            return new CssProperty(location, data[0].trim(), cssValue);
        } else {
            throw new CssException("Found a text that isn't a property or a comment at "+location);
        }
    }

    public static CssObject findObject(String sourceLocation, String name) throws CssException {
        for(CssAnnotation annotation : annotations) {
            if(annotation.getName().equals(name)) {
                return new CssObject.AnnotationObject(sourceLocation, annotation);
            }
        }
        List<CompoundCssSelector> selectors = buildSelectors(sourceLocation, name);
        if(!selectors.isEmpty()) {
            return new CssObject.BasicCssObject(sourceLocation, selectors);
        }
        throw new CssException("No css selector found at "+sourceLocation);
    }

    public static List<CompoundCssSelector> buildSelectors(String sourceLocation, String name) throws CssException {
        List<CompoundCssSelector> selectors = new ArrayList<>();
        CompoundCssSelector.Builder bu = new CompoundCssSelector.Builder();
        StringBuilder currentSelector = new StringBuilder();

        CssSelectorCombinator prevCombinator = null;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean b = c == ':' || i == name.length()-1;
            CssSelectorCombinator combinator = null;
            if(!b) {
                for (CssSelectorCombinator combine : CssSelectorCombinator.values()) {
                    if(combine.getLetter() == c) {
                        b = true;
                        combinator = combine;
                        break;
                    }
                }
            }
            //System.out.println("Found "+b+" "+combinator+" and trying "+c+" for "+ currentSelector);
            if(b) {
                if(i == name.length()-1)
                    currentSelector.append(c);
                String str = currentSelector.toString();
                if(!str.isEmpty()) {
                    if(str.startsWith(":")) {
                        bu.withPseudo(str.substring(1));
                    } else {
                        bu.withChild(sourceLocation, getSelector(sourceLocation, str));
                    }
                }
                currentSelector = new StringBuilder();
                if(prevCombinator != null) {
                    if(prevCombinator != CssSelectorCombinator.BLANK && combinator != CssSelectorCombinator.BLANK) { //Two modifier without selector between
                        throw new CssException("Only one modifier is permitted between each css selector. At "+sourceLocation);
                    }
                }
                if(combinator != null && (prevCombinator == null || combinator != CssSelectorCombinator.BLANK)) {
                    prevCombinator = combinator;
                }
                /*if(combinator == CssSelectorCombinator.ENUM) {
                    selectors.add(bu.build());
                    bu = new CompoundCssSelector.Builder();
                } else if(combinator != null) {
                    bu.withCombinator(combinator);
                } else {
                    currentSelector.append(c);
                }*/
                if(combinator == null) { // ":"
                    currentSelector.append(c);
                }
            } else {
                if(prevCombinator != null) {
                    //System.out.println("Combine : "+prevCombinator);
                    if(prevCombinator == CssSelectorCombinator.ENUM) {
                        selectors.add(bu.build());
                        bu = new CompoundCssSelector.Builder();
                    } else {
                        bu.withCombinator(prevCombinator);
                    }
                    prevCombinator = null;
                }
                currentSelector.append(c);
            }
        }
        selectors.add(bu.build());
        return selectors;
    }

    public static CssSelector<?> getSelector(String sourceLocation, String rawSelector) {
        if (rawSelector.startsWith(".")) //Class
        {
            return new CssSelector<>(CssSelector.EnumSelectorType.CLASS, rawSelector.substring(1));
        } else if (rawSelector.startsWith("#")) //Id
        {
            return new CssSelector<>(CssSelector.EnumSelectorType.ID, rawSelector.substring(1));
        } else //Component type
        {
            EnumComponentType componentType = EnumComponentType.fromString(rawSelector);
            if (componentType == null)
                throw new IllegalArgumentException("Component type not supported " + rawSelector + " at " + sourceLocation);
            return new CssSelector<>(CssSelector.EnumSelectorType.COMPONENT_TYPE, componentType);
        }
    }
}
