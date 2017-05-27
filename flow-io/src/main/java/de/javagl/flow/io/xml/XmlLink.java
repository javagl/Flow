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

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.javagl.flow.link.Link;
import de.javagl.flow.link.Links;
import de.javagl.flow.module.Module;

/**
 * XML handling for {@link Link} instances
 */
class XmlLink
{
    /**
     * Parse the objects from the given node.
     * 
     * @param node The node
     * @param idToModule The mapping from IDs to {@link Module} instances  
     * @return The objects
     * @throws XmlException If the object can not be parsed
     */
    static Set<Link> parseLinks(Node node, Map<String, Module> idToModule)
    {
        XmlUtils.verifyNodeName(node, "links");
        
        Set<Link> links = new LinkedHashSet<Link>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("linkInstance"))
            {
                parseLinkInstance(child, idToModule, links);
            }
        }
        return links;
    }

    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @param idToModule The mapping from IDs to {@link Module} instances  
     * @param links The set that will store the result
     * @throws XmlException If the object can not be parsed
     */
    private static void parseLinkInstance(
        Node node, Map<String, Module> idToModule, Set<Link> links)
    {
        XmlUtils.verifyNodeName(node, "linkInstance");

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("link"))
            {
                Link link = parseLink(child, idToModule);
                links.add(link);
            }
        }
    }
    
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @param idToModule The mapping from IDs to {@link Module} instances  
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    private static Link parseLink(Node node, Map<String, Module> idToModule)
    {
        XmlUtils.verifyNodeName(node, "link");
        
        Module sourceModule = null;
        int sourceModuleOutputSlotIndex = -1;
        Module targetModule = null;
        int targetModuleInputSlotIndex = -1;
        
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("sourceModule"))
            {
                sourceModule = XmlUtils.resolve(child, "idref", idToModule);
            } else if (childName
                .equalsIgnoreCase("sourceModuleOutputSlotIndex"))
            {
                sourceModuleOutputSlotIndex = XmlUtils.readInt(child);
            } else if (childName.equalsIgnoreCase("targetModule"))
            {
                targetModule = XmlUtils.resolve(child, "idref", idToModule);
            } else if (childName.equalsIgnoreCase("targetModuleInputSlotIndex"))
            {
                targetModuleInputSlotIndex = XmlUtils.readInt(child);
            }
        }
        
        if (sourceModule == null)
        {
            throw new XmlException("Could not parse Flow - " 
                + "no source module");
        }
        if (sourceModuleOutputSlotIndex == -1)
        {
            throw new XmlException("Could not parse Flow - " 
                + "no source output slot index");
        }
        if (targetModule == null)
        {
            throw new XmlException("Could not parse Flow - " 
                + "no target module'");
        }
        if (targetModuleInputSlotIndex == -1)
        {
            throw new XmlException("Could not parse Flow - " 
                + "no target input slot index");
        }
        Link link = Links.create(
            sourceModule, sourceModuleOutputSlotIndex, 
            targetModule, targetModuleInputSlotIndex);
        return link;
    }
    

    /**
     * Create the node for the given objects
     * 
     * @param links The objects
     * @param moduleToId The mapping from {@link Module} objects to IDs
     * @return The node
     */
    static Element createNode(Iterable<? extends Link> links,
        Map<Module, String> moduleToId)
    {
        Document d = XmlUtils.getDefaultDocument();
        Element linksNode = d.createElement("links");
        for (Link link : links)
        {
            Module sourceModule = link.getSourceSlot().getModule();
            String sourceId = moduleToId.get(sourceModule);
            int sourceIndex = link.getSourceSlot().getIndex();
            
            Module targetModule = link.getTargetSlot().getModule();
            String targetId = moduleToId.get(targetModule);
            int targetIndex = link.getTargetSlot().getIndex();
            
            Element linkInstanceNode = d.createElement("linkInstance");
            Element linkNode = d.createElement("link");
            
            Element sourceModuleNode = d.createElement("sourceModule");
            sourceModuleNode.setAttribute("idref", sourceId);
            linkNode.appendChild(sourceModuleNode);
            linkNode.appendChild(XmlUtils.createTextNode(
                "sourceModuleOutputSlotIndex", sourceIndex));

            Element targetModuleNode = d.createElement("targetModule");
            targetModuleNode.setAttribute("idref", targetId);
            linkNode.appendChild(targetModuleNode);
            linkNode.appendChild(XmlUtils.createTextNode(
                "targetModuleInputSlotIndex", targetIndex));
            
            linkInstanceNode.appendChild(linkNode);
            linksNode.appendChild(linkInstanceNode);
        }
        return linksNode;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private XmlLink()
    {
        // Private constructor to prevent instantiation
    }
    

}
