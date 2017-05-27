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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.javagl.flow.Flow;
import de.javagl.flow.Flows;
import de.javagl.flow.MutableFlow;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.repository.Repository;
import de.javagl.flow.workspace.FlowLayout;
import de.javagl.flow.workspace.FlowLayouts;
import de.javagl.flow.workspace.FlowWorkspace;
import de.javagl.flow.workspace.FlowWorkspaces;
import de.javagl.flow.workspace.MutableFlowLayout;
import de.javagl.flow.workspace.MutableFlowWorkspace;

/**
 * XML handling for {@link FlowWorkspace} instances
 */
public class XmlFlowWorkspace
{
    /**
     * Parse a {@link MutableFlowWorkspace} from the given XML node
     * 
     * @param node The node
     * @return The {@link MutableFlowWorkspace}
     * @throws XmlException If the object can not be parsed
     */
    public static MutableFlowWorkspace parse(Node node)
    {
        XmlUtils.verifyNodeName(node, "flowWorkspace");

        MutableFlow flow = Flows.create();
        MutableFlowLayout flowLayout = FlowLayouts.create();

        Map<String, Module> idToModule = new LinkedHashMap<String, Module>();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("flow"))
            {
                flow = XmlFlow.parse(child, idToModule);
            }
        }

        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node child = childNodes.item(i);
            String childName = child.getNodeName();
            if (childName.equalsIgnoreCase("flowLayout"))
            {
                flowLayout = XmlFlowLayout.parse(child, idToModule);
            }
        }

        MutableFlowWorkspace flowWorkspace =
            FlowWorkspaces.create(flow, flowLayout);
        return flowWorkspace;
    }

    /**
     * Create an XML node for the given {@link FlowWorkspace}
     * 
     * @param flowWorkspace The {@link FlowWorkspace}
     * @param moduleCreatorRepository The {@link Repository} of
     * {@link ModuleCreator} instances
     * @return The XML node
     * @throws XmlException If the node can not be created
     */
    public static Node createNode(FlowWorkspace flowWorkspace,
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository)
    {
        Document d = XmlUtils.getDefaultDocument();
        Element flowWorkspaceNode = d.createElement("flowWorkspace");

        Map<Module, String> moduleToId = new HashMap<Module, String>();

        Flow flow = flowWorkspace.getFlow();
        Node flowNode =
            XmlFlow.createNode(flow, moduleToId, moduleCreatorRepository);
        flowWorkspaceNode.appendChild(flowNode);

        FlowLayout flowLayout = flowWorkspace.getFlowLayout();
        Node flowLayoutNode = XmlFlowLayout.createNode(flowLayout, moduleToId);
        flowWorkspaceNode.appendChild(flowLayoutNode);

        return flowWorkspaceNode;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private XmlFlowWorkspace()
    {
        // Private constructor to prevent instantiation
    }
    

}