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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.creation.ModuleCreatorInstantiator;
import de.javagl.flow.repository.Repository;

/**
 * XML handling for {@link Module} instances
 */
class XmlModule
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(XmlModule.class.getName());

    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    static Map<String, Module> parseModules(Node node)
    {
        XmlUtils.verifyNodeName(node, "modules");
        
        Map<String, Module> idToModule = new LinkedHashMap<String, Module>();
        NodeList childNodes = node.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("moduleInstance"))
            {
                parseModuleInstance(child, idToModule);
            }
        }
        return idToModule;
    }
    
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @param idToModule The mapping from IDs to {@link Module} instances
     * @throws XmlException If the object can not be parsed
     */
    private static void parseModuleInstance(
        Node node, Map<String, Module> idToModule)
    {
        XmlUtils.verifyNodeName(node, "moduleInstance");
        
        String id = XmlUtils.getRequiredAttributeValue(node, "id");
        if (idToModule.containsKey(id))
        {
            throw new XmlException(
                "Could not parse Flow - " + "duplicate id '" + id + "'");
        }
        NodeList childNodes = node.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("module"))
            {
                Module module = parseModule(child);
                idToModule.put(id, module);
            }
        }
    }
    
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    private static Module parseModule(Node node)
    {
        XmlUtils.verifyNodeName(node, "module");
        
        String instantiationString = 
            getChildContents(node, "instantiationString");
        if (instantiationString == null)
        {
            throw new XmlException("Could not parse Flow - "
                + "module has no instantiationString");
        }

        Module module = null;
        try
        {
            ModuleCreator moduleCreator = 
                ModuleCreatorInstantiator.createFromInstantiationString(
                    instantiationString.trim());
            module = moduleCreator.createModule();
        }
        catch (IllegalArgumentException e)
        {
            throw new XmlException("Could not parse Flow - " 
                + "invalid instantiationString: "
                + "'" + instantiationString + "'", e);
        }
        if (module == null)
        {
            throw new XmlException("Could not parse Flow - "
                + "could not instantiate module '" + instantiationString + "'");
        }
        parseConfiguration(node, module);
        
        logger.info("Read module "+module);
        return module;
    }
    
    
    /**
     * Parse the object from the given node.
     * 
     * @param moduleNode The node
     * @param module The {@link Module}
     * @throws XmlException If the object can not be parsed
     */
    private static void parseConfiguration(Node moduleNode, Module module)
    {
        XmlUtils.verifyNodeName(moduleNode, "module");

        NodeList childNodes = moduleNode.getChildNodes();
        for (int i=0; i< childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("configuration"))
            {
                Object configuration = 
                    XmlConfiguration.parseConfiguration(child);
                if (configuration != null)
                {
                    module.setConfiguration(configuration);
                }
            }
        }
    }

    
    
    
    /**
     * Searches the given node for a child with the given name, and returns
     * the contents of its first child.
     *  
     * @param node The node
     * @param requiredChildName The name of the child
     * @return The node contents
     */
    private static String getChildContents(Node node, String requiredChildName)
    {
        NodeList childNodes = node.getChildNodes();
        for (int i=0; i< childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase(requiredChildName))
            {
                String value = child.getFirstChild().getNodeValue();
                return value;
            }
        }
        return null;
    }

    
    /**
     * Create the node for the given objects
     * 
     * @param modules The objects
     * @param moduleToId The mapping from {@link Module} objects to IDs
     * @param moduleCreatorRepository The {@link Repository} of 
     * {@link ModuleCreator} instances
     * @return The node
     * @throws XmlException If the node can not be created
     */
    static Node createNode(Iterable<? extends Module> modules, 
        Map<Module, String> moduleToId, 
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository)
    {
        Document d = XmlUtils.getDefaultDocument();
        Element modulesNode = d.createElement("modules");
        int idCounter = 0;
        for (Module module : modules)
        {
            Element moduleInstanceNode = d.createElement("moduleInstance");

            String id = createModuleId(module, idCounter);
            idCounter++;
            moduleToId.put(module, id);
            moduleInstanceNode.setAttribute("id", id);
            
            Element moduleNode = d.createElement("module");
            moduleInstanceNode.appendChild(moduleNode);

            ModuleInfo moduleInfo = module.getModuleInfo();
            ModuleCreator moduleCreator = 
                moduleCreatorRepository.get(moduleInfo);
            moduleNode.appendChild(XmlUtils.createTextNode(
                "instantiationString", 
                moduleCreator.getInstantiationString()));
            
            Object configuration = module.getConfiguration();
            if (configuration != null)
            {
                Node configurationNode = 
                    XmlConfiguration.createNode(configuration);
                moduleNode.appendChild(configurationNode);
            }
            
            modulesNode.appendChild(moduleInstanceNode);
        }
        return modulesNode;
    }
    
    /**
     * Create a {@link Module} ID
     * 
     * @param module The {@link Module}
     * @param idCounter The current ID counter
     * @return The ID
     */
    private static String createModuleId(Module module, int idCounter)
    {
        String moduleName = module.getModuleInfo().getName();
        String escapedModuleName = escapeNonLetterOrDigit(moduleName);
        String id = "module_"+escapedModuleName+"_"+String.valueOf(idCounter);
        return id;
    }
    
    /**
     * Replace all non-letter-or-digit characters with <code>'_'</code>
     * 
     * @param string The string
     * @return The result
     */
    private static String escapeNonLetterOrDigit(String string)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            if (Character.isLetterOrDigit(c))
            {
                sb.append(c);
            }
            else
            {
                sb.append("_");
            }
        }
        return sb.toString();
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private XmlModule()
    {
        // Private constructor to prevent instantiation
    }
    
}
