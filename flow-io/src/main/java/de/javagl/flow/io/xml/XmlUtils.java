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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility methods related to XML handling
 */
public class XmlUtils
{
    /**
     * A default XML document
     */
    private static Document defaultDocument = null;
    
    /**
     * Returns a default XML document
     * 
     * @return The default XML document
     */
    public static Document getDefaultDocument()
    {
        if (defaultDocument == null)
        {
            DocumentBuilderFactory documentBuilderFactory = 
                DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            try
            {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            }
            catch (ParserConfigurationException e)
            {
                // Can not happen, since it's the default configuration
                throw new XmlException("Could not create default document", e);
            }
            defaultDocument = documentBuilder.newDocument();
        }
        return defaultDocument;
    }
    
    /**
     * Creates a formatted String representation of the given XML node
     * 
     * @param node The node
     * @return The formatted String representation of the given node
     * @throws XmlException If there was an error while writing. 
     * This should never be the case for this method, because the
     * XML is written into a String, and no exceptional situation
     * (like IOException) can happen here.
     */
    public static String toString(Node node)
    {
        StringWriter stringWriter = new StringWriter();
        write(node, stringWriter);
        return stringWriter.toString();
    }
    
    /**
     * Writes a formatted String representation of the given XML node
     * to the given output stream. The caller is responsible for 
     * closing the given stream.
     * 
     * @param node The node
     * @param outputStream The output stream to write to
     * @throws XmlException If there was an error while writing
     */
    public static void write(Node node, OutputStream outputStream)
    {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        write(node, writer);
    }
    
    /**
     * Writes a formatted String representation of the given XML node
     * to the given writer.
     * 
     * @param node The node 
     * @param writer The writer to write to
     * @throws XmlException If there was an error while writing
     */
    private static void write(Node node, Writer writer)
    {
        TransformerFactory transformerFactory = 
            TransformerFactory.newInstance();
        final int indent = 4;
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = null;
        try
        {
            transformer = transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException canNotHappen)
        {
            // Can not happen here
            throw new XmlException(
                "Could not create transformer", canNotHappen);
        } 
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DOMSource source = new DOMSource(node);

        StreamResult xmlOutput = new StreamResult(writer);
        try
        {
            transformer.transform(source, xmlOutput);
        }
        catch (TransformerException e)
        {
            throw new XmlException("Could not transform node", e);
        }
    }
    
    /**
     * Creates an XML node by reading the contents of the given input stream.
     * 
     * @param inputStream The input stream to read from
     * @return The parsed node
     * @throws XmlException If there was an error while reading
     */
    public static Node read(InputStream inputStream) throws XmlException
    {
        try
        {
            DocumentBuilderFactory documentBuilderFactory = 
                DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = 
                documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Node node = document.getDocumentElement();
            return node;
        } 
        catch (ParserConfigurationException canNotHappen)
        {
            // Can not happen with a default configuration
            throw new XmlException("Could not create parser", canNotHappen);
        } 
        catch (SAXException e)
        {
            throw new XmlException("XML parsing error", e);
        }
        catch (IOException e)
        {
            throw new XmlException("IO error while reading XML", e);
        }

    }
    
    /**
     * Creates an XML node from the 
     * {@link #getDefaultDocument() default document} whose only child
     * is a text node that contains the string representation of the
     * given object
     * 
     * @param tagName The tag name for the node
     * @param contents The object whose string representation will be
     * the contents of the text node
     * @return The node
     */
    static Node createTextNode(String tagName, Object contents)
    {
        Document d = XmlUtils.getDefaultDocument();
        Element node = d.createElement(tagName);
        node.appendChild(d.createTextNode(String.valueOf(contents)));
        return node;
    }
    
    
    /**
     * Returns the attribute with the given name from the given node.
     * If the respective attribute could not be obtained, the given
     * default value will be returned
     * 
     * @param node The node to obtain the attribute from
     * @param attributeName The name of the attribute
     * @param defaultValue The default value to return when the specified
     * attribute could not be obtained
     * @return The value of the attribute, or the default value
     */
    static String getAttributeValue(
        Node node, String attributeName, String defaultValue)
    {
        NamedNodeMap attributes = node.getAttributes();
        Node attributeNode = attributes.getNamedItem(attributeName);
        if (attributeNode == null)
        {
            return defaultValue;
        }
        String value = attributeNode.getNodeValue();
        if (value == null)
        {
            return defaultValue;
        }
        return value;
    }
    
    
    /**
     * Returns the attribute with the given name from the given node.
     * 
     * @param node The node to obtain the attribute from
     * @param attributeName The name of the attribute
     * @return The value of the attribute
     * @throws XmlException If no value of the attribute with the given 
     * name could be obtained.
     */
    static String getRequiredAttributeValue(Node node, String attributeName)
    {
        NamedNodeMap attributes = node.getAttributes();
        Node attributeNode = attributes.getNamedItem(attributeName);
        if (attributeNode == null)
        {
            throw new XmlException(
                "No attribute with name \"" + attributeName + "\" found");
        }
        String value = attributeNode.getNodeValue();
        if (value == null)
        {
            throw new XmlException(
                "Attribute with name \"" + attributeName + "\" has no value");
        }
        return value;
    }
    
    
    
    /**
     * Removes any whitespace from the text nodes that are descendants
     * of the given node. Any text node that becomes empty due to this
     * (that is, any text node that only contained whitespaces) will
     * be removed.
     * 
     * @param node The node to remove whitespaces from
     */
    static void removeWhitespace(Node node)
    {
        NodeList childList = node.getChildNodes();
        List<Node> toRemove = new ArrayList<Node>();
        for (int i = 0; i < childList.getLength(); i++)
        {
            Node child = childList.item(i);
            if (child.getNodeType() == Node.TEXT_NODE)
            {
                String text = child.getTextContent();
                String trimmed = text.trim();
                if (trimmed.isEmpty())
                {
                    toRemove.add(child);
                }
                else if (trimmed.length() < text.length())
                {
                    child.setTextContent(trimmed);
                }
            }
            removeWhitespace(child);
        }
        for (Node c : toRemove)
        {
            node.removeChild(c);
        }
    }
    

    /**
     * Verify that the name of the given node matches the expected tag name
     * (ignoring upper/lowercase), and throw an XmlException if this is not
     * the case.
     * 
     * @param node The node
     * @param expected The expected tag name
     * @throws XmlException If the node name does not match the expected name
     */ 
    static void verifyNodeName(Node node, String expected)
    {
        if (!node.getNodeName().equalsIgnoreCase(expected))
        {
            throw new XmlException(
                "Expected <" + expected + "> tag, " 
                + "but found <" + node.getNodeName() + ">");
        }
    }
    
    
    /**
     * Resolve the value in the given map whose key is the value of 
     * the specified attribute. 
     * 
     * @param <T> The type of the elements in the map
     * @param node The node
     * @param attributeName The name of the attribute
     * @param map The map 
     * @return The value in the map whose key is the value of the
     * specified attribute
     * @throws XmlException If either the given node does not contain
     * an attribute with the given name, or the map does not contain
     * a value for the respective attribute value
     */
    static <T> T resolve(
        Node node, String attributeName, Map<String, ? extends T> map)
    {
        String id = XmlUtils.getAttributeValue(node, attributeName, null);
        if (id == null)
        {
            throw new XmlException(
                "No attribute \"" + attributeName + "\" found");
        }
        T result = map.get(id);
        if (result == null)
        {
            throw new XmlException(
                "Could not resolve value with id \"" + id + "\"");
        }
        return result;
    }
    
    
    /**
     * Parse an int value from the first child of the given node.
     * 
     * @param node The node
     * @return The int value
     * @throws XmlException If no int value could be parsed
     */
    static int readInt(Node node)
    {
        String value = node.getFirstChild().getNodeValue();
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            throw new XmlException(
                "Expected int value, found \"" + value + "\"", e);
        }
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private XmlUtils()
    {
        // Private constructor to prevent instantiation
    }
}
