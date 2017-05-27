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
package de.javagl.flow.io.xml;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.javagl.flow.module.Module;
import de.javagl.flow.workspace.FlowLayout;
import de.javagl.flow.workspace.FlowLayouts;
import de.javagl.flow.workspace.MutableFlowLayout;

/**
 * XML handling for {@link FlowLayout} instances
 */
class XmlFlowLayout
{
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @param idToModule The mapping from IDs to {@link Module} instances
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    static MutableFlowLayout parse(Node node, 
        Map<String, Module> idToModule)
    {
        XmlUtils.verifyNodeName(node, "flowLayout");

        MutableFlowLayout flowLayout = FlowLayouts.create();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("moduleBounds"))
            {
                Module module = XmlUtils.resolve(child, "idref", idToModule);
                Rectangle2D bounds = parseModuleBounds(child);
                flowLayout.setBounds(module, bounds);
            }
        }

        return flowLayout;
    }
    
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    private static Rectangle2D parseModuleBounds(Node node)
    {
        XmlUtils.verifyNodeName(node, "moduleBounds");

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("bounds"))
            {
                Rectangle2D bounds = parseBounds(child);
                return bounds;
            }
        }
        return null;
    }
    
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    private static Rectangle2D parseBounds(Node node)
    {
        XmlUtils.verifyNodeName(node, "bounds");

        String value = node.getFirstChild().getNodeValue();
        StringTokenizer st = new StringTokenizer(value, ", ");
        if (st.countTokens() != 4)
        {
            throw new XmlException("Could not parse bounds - "
                + "bounds invalid: '" + value + "'");
        }
        String sx = st.nextToken();
        String sy = st.nextToken();
        String sw = st.nextToken();
        String sh = st.nextToken();
        try
        {
            double x = Double.parseDouble(sx);
            double y = Double.parseDouble(sy);
            double w = Double.parseDouble(sw);
            double h = Double.parseDouble(sh);
            return new Rectangle2D.Double(x, y, w, h);
        }
        catch (NumberFormatException e)
        {
            throw new XmlException("Could not parse bounds - " +
                "bounds invalid: '" + value + "'");
        }
    }


    /**
     * Create the node for the given object
     * 
     * @param flowLayout The object
     * @param moduleToId The mapping from {@link Module} objects to IDs
     * @return The node
     * @throws XmlException If the node can not be created
     */
    static Node createNode(FlowLayout flowLayout, 
        Map<Module, String> moduleToId)
    {
        Document d = XmlUtils.getDefaultDocument();
        Element flowLayoutNode = d.createElement("flowLayout");

        for (Entry<Module, Rectangle2D> entry : 
             flowLayout.getEntries().entrySet())
        {
            Module module = entry.getKey();
            Rectangle2D bounds = entry.getValue();
            
            Element moduleBoundsNode = d.createElement("moduleBounds");
            String id = moduleToId.get(module);
            moduleBoundsNode.setAttribute("idref", id);

            Element boundsNode = d.createElement("bounds");

            String p = 
                String.valueOf(bounds.getX()) + " " +
                String.valueOf(bounds.getY()) + " " +
                String.valueOf(bounds.getWidth()) + " " +
                String.valueOf(bounds.getHeight());
            boundsNode.appendChild(d.createTextNode(p));
            
            moduleBoundsNode.appendChild(boundsNode);
            flowLayoutNode.appendChild(moduleBoundsNode);
        }
        
        return flowLayoutNode;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private XmlFlowLayout()
    {
        // Private constructor to prevent instantiation
    }
    
}