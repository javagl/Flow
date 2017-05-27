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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.javagl.flow.module.Module;

/**
 * XML handling for {@link Module#getConfiguration() module configurations}
 */
class XmlConfiguration
{
    /**
     * Parse the object from the given node.
     * 
     * @param node The node
     * @return The object
     * @throws XmlException If the object can not be parsed
     */
    static Object parseConfiguration(Node node)
    {
        XmlUtils.verifyNodeName(node, "configuration");

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            if (child.getNodeName().equalsIgnoreCase("java"))
            {
                return decode(child);
            }
        }
        return null;
    }
    
    /**
     * Decode the configuration object from the given node.
     * 
     * @param node The node
     * @return The configuration object
     * @throws XmlException If the object can not be parsed
     */
    private static Object decode(Node node)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlUtils.write(node, baos);
        
        ByteArrayInputStream bais = 
            new ByteArrayInputStream(baos.toByteArray());
        XMLDecoder decoder = new XMLDecoder(bais);
        Object configuration = decoder.readObject();
        decoder.close();
        return configuration;
    }

    /**
     * Create the node for the given object
     *  
     * @param configuration The object
     * @return The node
     * @throws XmlException If the node can not be created
     */
    static Node createNode(Object configuration)
    {
        Document d = XmlUtils.getDefaultDocument();
        Element configurationNode = d.createElement("configuration");
        Node node = encode(configuration);
        configurationNode.appendChild(d.importNode(node, true));
        return configurationNode;
    }
    
    /**
     * Create the node for the given object
     *  
     * @param configuration The object
     * @return The node
     * @throws XmlException If the node can not be created
     */
    private static Node encode(Object configuration)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(baos);
        encoder.writeObject(configuration);
        encoder.close();

        ByteArrayInputStream bais = 
            new ByteArrayInputStream(baos.toByteArray());
        Node node = XmlUtils.read(bais);
        XmlUtils.removeWhitespace(node);
        return node;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private XmlConfiguration()
    {
        // Private constructor to prevent instantiation
    }

}
