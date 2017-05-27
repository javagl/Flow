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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.javagl.flow.Flow;
import de.javagl.flow.Flows;
import de.javagl.flow.MutableFlow;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.repository.Repository;

/**
 * XML handling for {@link Flow} instances
 */
class XmlFlow
{
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @param idToModule The mapping from IDs to {@link Module} instances
     * @param moduleCreatorRepository The {@link Repository} of 
     * {@link ModuleCreator} instances
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    static MutableFlow parse(Node node, Map<String, Module> idToModule,
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository)
    {
        XmlUtils.verifyNodeName(node, "flow");

        MutableFlow flow = Flows.create();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("modules"))
            {
                Map<String, Module> localIdToModule =
                    XmlModule.parseModules(child, moduleCreatorRepository);
                idToModule.putAll(localIdToModule);
            }
        }
        
        for (Module module : idToModule.values())
        {
            flow.addModule(module);
        }
        
        Set<Link> links = Collections.emptySet();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("links"))
            {
                links = XmlLink.parseLinks(child, idToModule);
            }
        }
        
        for (Link link : links)
        {
            flow.addLink(link);
        }
        
        return flow;
    }
    
    /**
     * Create the node for the given object
     * 
     * @param flow The object
     * @param moduleToId The mapping from {@link Module} objects to IDs
     * @param moduleCreatorRepository The {@link Repository} of 
     * {@link ModuleCreator} instances
     * @return The node
     */
    static Node createNode(Flow flow, 
        Map<Module, String> moduleToId, 
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository)
    {
        Document d = XmlUtils.getDefaultDocument();
        Element flowNode = d.createElement("flow");

        Node modulesNode = XmlModule.createNode(flow.getModules(), 
            moduleToId, moduleCreatorRepository); 
        flowNode.appendChild(modulesNode);
        
        Element linksNode = XmlLink.createNode(
            flow.getLinks(), moduleToId);
        
        flowNode.appendChild(linksNode);
        return flowNode;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private XmlFlow()
    {
        // Private constructor to prevent instantiation
    }
    
    
}