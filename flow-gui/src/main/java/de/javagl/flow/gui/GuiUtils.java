/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.flow.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.SlotInfo;

/**
 * Utility methods related to the flow GUI
 */
class GuiUtils
{
    /**
     * The list of package names that are "well known" and should be
     * removed from type names.
     */
    private static final List<String> KNOWN_PACKAGE_NAMES;
    
    // Initialization of the KNOWN_PACKAGE_NAMES 
    static
    {
        // TODO Where should they be defined, actually?
        List<String> knownPackageNames = Arrays.asList(
            "java.lang.", 
            "java.awt.geom.",
            "java.awt.",
            "java.util.stream.",
            "java.util.function.",
            "java.util.",
            "de.javagl.flow.",
            "de.javagl.data.",
            "de.javagl.timeseries.",
            "com.google.common.collect.",
            "com.google.common.",
            "org.apache.commons.math3.stat.clustering."
        );
        Collections.sort(knownPackageNames, 
            (s0, s1) -> Integer.compare(s0.length(), s1.length()));
        KNOWN_PACKAGE_NAMES = 
            Collections.unmodifiableList(knownPackageNames);
    }
    
    /**
     * Creates a default HTML string representation for the
     * given {@link ModuleInfo}. 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @return The HTML string describing the {@link ModuleInfo}
     */
    static String moduleInfoString(ModuleInfo moduleInfo)
    {
        return stringFor(null, moduleInfo);
    }
        
    /**
     * Creates a default HTML string representation for the
     * given {@link Module}. 
     * 
     * @param module The {@link Module}
     * @return The HTML string describing the {@link Module}
     */
    static String moduleString(Module module)
    {
        return stringFor(module, 
            (module == null ? null : module.getModuleInfo()));
    }
        
    /**
     * Creates a default HTML string representation for the
     * given {@link Module} and {@link ModuleInfo} 
     * 
     * @param module The {@link Module}
     * @param moduleInfo The {@link ModuleInfo}
     * @return The HTML string describing the {@link Module}
     */
    private static String stringFor(Module module, ModuleInfo moduleInfo)
    {
        if (moduleInfo == null)
        {
            return "";
        }
        String indent = "&nbsp;&nbsp;&nbsp;&nbsp;";

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<b>" + moduleInfo.getName() + "</b>");
        if (module != null)
        {
            sb.append("<br>" + indent + String.valueOf(module));
        }
        sb.append("<br>");
        sb.append(moduleInfo.getDescription()+"<br>");

        List<SlotInfo> inputSlotInfos = moduleInfo.getInputSlotInfos();
        if (!inputSlotInfos.isEmpty())
        {
            sb.append("Inputs:<br>");
            for (int i = 0; i < inputSlotInfos.size(); i++)
            {
                sb.append(indent);
                sb.append("<b>");
                sb.append(inputSlotInfos.get(i).getName());
                sb.append("</b>");
                sb.append(" : ");
                sb.append(inputSlotInfos.get(i).getDescription());
                sb.append("<br>");
                
                if (module == null)
                {
                    sb.append(indent);
                    sb.append(indent);
                    sb.append("Type: ");
                    sb.append("<i>");
                    Type inputType = inputSlotInfos.get(i).getType();
                    sb.append(escapeTypeName(inputType));
                    sb.append("</i>");
                    sb.append("<br>");
                }
                else
                {
                    List<InputSlot> inputSlots = module.getInputSlots();
                    InputSlot inputSlot = inputSlots.get(i);

                    sb.append(indent);
                    sb.append(indent);
                    sb.append("Expected type: ");
                    sb.append("<i>");
                    Type expectedType = inputSlot.getExpectedType();
                    sb.append(escapeTypeName(expectedType));
                    sb.append("</i>");
                    sb.append("<br>");

                    sb.append(indent);
                    sb.append(indent);
                    sb.append("Actual type &nbsp;&nbsp;&nbsp; : ");
                    sb.append("<i>");
                    Type actualType = inputSlot.getActualType();
                    sb.append(escapeTypeName(actualType));
                    sb.append("</i>");
                    sb.append("<br>");
                }
            }
        }
        
        List<SlotInfo> outputSlotInfos = moduleInfo.getOutputSlotInfos();
        if (!outputSlotInfos.isEmpty())
        {
            sb.append("Outputs:<br>");
            for (int i = 0; i < outputSlotInfos.size(); i++)
            {
                sb.append(indent);
                sb.append("<b>");
                sb.append(outputSlotInfos.get(i).getName());
                sb.append("</b>");
                sb.append(" : ");
                sb.append(outputSlotInfos.get(i).getDescription());
                sb.append("<br>");

                if (module == null)
                {
                    sb.append(indent);
                    sb.append(indent);
                    sb.append("<i>");
                    Type outputType = outputSlotInfos.get(i).getType();
                    sb.append(escapeTypeName(outputType));
                    sb.append("</i>");
                    sb.append("<br>");
                }
                else
                {
                    List<OutputSlot> outputSlots = module.getOutputSlots();
                    OutputSlot outputSlot = outputSlots.get(i);

                    sb.append(indent);
                    sb.append(indent);
                    sb.append("<i>");
                    Type outputType = outputSlot.getActualType();
                    sb.append(escapeTypeName(outputType));
                    sb.append("</i>");
                    sb.append("<br>");
                }
            }
        }
        
        sb.append("</html>");
        return sb.toString();
    }
   
    
    
    /**
     * Escapes the string representation of the given type. That is, it 
     * escapes special HTML characters like the &lt; and &gt; brackets of 
     * generics, and removes the "class"- or "interface" prefix, and some 
     * default package prefixes for well-known types.
     * 
     * @param type The type
     * @return The escaped string representation of the type token
     */
    private static String escapeTypeName(Type type)
    {
        return escapeTypeName(String.valueOf(type));
    }
    
    /**
     * Escapes the given string representation of a type. That is, it 
     * escapes special HTML characters like the &lt; and &gt; brackets of 
     * generics, and removes some default package prefixes for 
     * well-known types.
     * 
     * @param s The type token string
     * @return The escaped string representation of the type token
     */
    static String escapeTypeName(String s)
    {
        s = escapeHtml(s);
        s = s.trim();
        
        if (s.startsWith("class "))
        {
            s = s.substring(6);
        }
        if (s.startsWith("interface "))
        {
            s = s.substring(10);
        }
        return removeKnownPackageNamePrefixes(s);
    }
    
    
    /**
     * Remove all well known package name prefixes from the given string.
     * For example, the String <code>"java.lang.Integer"</code> will
     * become <code>"Integer"</code>.<br>
     * <br>
     * The exact set of "well known package names" is not specified.
     * 
     * @param string The input string
     * @return The result string
     */
    private static String removeKnownPackageNamePrefixes(String string)
    {
        String s = string;
        for (String p : KNOWN_PACKAGE_NAMES)
        {
            s = s.replaceAll(p, "");
        }
        return s;
    }
    
    /**
     * Escapes the given string so that it does not contain
     * some of the obvious, special HTML characters
     * 
     * @param s The string
     * @return The escaped string
     */
    private static String escapeHtml(String s)
    {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("\"", "&quot;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("<", "&lt;");
        return s;
    }
    
    
    /**
     * Create a text component that allows displaying HTML text,
     * pre-formatted for the use inside the flow package
     * 
     * @return The text component
     */
    static JTextComponent createDefaultEditor()
    {
        JEditorPane textComponent = new JEditorPane("text/html", "");
        textComponent.setEditable(false);
        textComponent.setDragEnabled(false);
        textComponent.setDropTarget(null);
        
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        String bodyRule = 
            "body { font-family: " + font.getFamily() + "; " +
            "font-size: " + font.getSize() + "pt; }";
        Document document = textComponent.getDocument();
        HTMLDocument htmlDocument = (HTMLDocument)document;
        htmlDocument.getStyleSheet().addRule(bodyRule);
        
        return textComponent;
    }
    
    /**
     * Set the drop target to <code>null</code> for the given component
     * and all its descendants, recursively.
     * 
     * @param component The component
     */
    static void disableDropTargetsDeep(Component component)
    {
        component.setDropTarget(null);
        if (component instanceof Container)
        {
            Container container = (Container)component;
            for (Component child : container.getComponents())
            {
                disableDropTargetsDeep(child);
            }
        }
    }
    
    
    /**
     * Set the location of the the given split pane to the given 
     * value later on the EDT, and validate the split pane
     * 
     * @param splitPane The split pane
     * @param location The location
     */
    static void setDividerLocation(
        final JSplitPane splitPane, final double location)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                splitPane.setDividerLocation(location);
                splitPane.validate();
            }
        });
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private GuiUtils()
    {
        // Private constructor to prevent instantiation
    }
}
