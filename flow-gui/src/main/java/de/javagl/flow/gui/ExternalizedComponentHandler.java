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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.javagl.common.ui.closeable.CloseablePanel;
import de.javagl.common.ui.panel.collapsible.AccordionPanel;
import de.javagl.flow.Flow;
import de.javagl.flow.FlowAdapter;
import de.javagl.flow.FlowEvent;
import de.javagl.flow.FlowListener;
import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewType;
import de.javagl.flow.module.view.ModuleViewTypes;
import de.javagl.flow.repository.Repository;

/**
 * A class handling the externalization of configuration- and visualization
 * components of a {@link ModuleComponent} into an external frame
 */
class ExternalizedComponentHandler
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(ExternalizedComponentHandler.class.getName());
    
    /**
     * The {@link Repository} of {@link ModuleCreator} instances
     */
    private final Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository;
    
    /**
     * The frame for the configuration components
     */
    private final JFrame configurationsFrame;

    /**
     * The accordion panel for the configurations
     */
    private final AccordionPanel configurationsAccordionPanel;

    /**
     * The frame for the visualization components
     */
    private final JFrame visualizationsFrame;
    
    /**
     * The accordion panel for the visualizations
     */
    private final AccordionPanel visualizationsAccordionPanel;
    
    /**
     * The mapping from {@link Module} instances to the containers that
     * have been added to the accordion for configuration components
     */
    private final Map<Module, JComponent> configurationComponentContainers;
    
    /**
     * The mapping from {@link Module} instances to the containers that
     * have been added to the accordion for visualization components
     */
    private final Map<Module, JComponent> visualizationComponentContainers;
    
    /**
     * The {@link FlowListener} that will be attached
     * to the {@link Flow} of the {@link FlowEditor}, 
     * and remove the components of removed modules
     */
    private final FlowListener flowListener = new FlowAdapter()
    {
        @Override
        public void moduleRemoved(FlowEvent flowEvent)
        {
            Module module = flowEvent.getModule();
            JComponent c = configurationComponentContainers.get(module);
            configurationsAccordionPanel.removeFromAccordion(c);
            JComponent v = visualizationComponentContainers.get(module);
            visualizationsAccordionPanel.removeFromAccordion(v);
        }
    };

    /**
     * The {@link FlowEditor}
     */
    private FlowEditor flowEditor;
    
    /**
     * Default constructor 
     * 
     * @param moduleCreatorRepository The {@link Repository} of 
     * {@link ModuleCreator} instances
     */
    ExternalizedComponentHandler(
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository)
    {
        this.moduleCreatorRepository = moduleCreatorRepository;
        
        configurationComponentContainers = 
            new LinkedHashMap<Module, JComponent>();
        visualizationComponentContainers = 
            new LinkedHashMap<Module, JComponent>();

        // XXX TODO Think about how to lay out the externalized frames
        
        configurationsFrame = new JFrame("Configurations");
        configurationsFrame.setDefaultCloseOperation(
            JFrame.HIDE_ON_CLOSE);
        configurationsAccordionPanel = new AccordionPanel();
        configurationsFrame.getContentPane()
            .add(configurationsAccordionPanel);
        configurationsFrame.setSize(400, 600);
        configurationsFrame.setLocation(2000, 0);
        configurationsFrame.setVisible(true);

        visualizationsFrame = new JFrame("Visualizations");
        visualizationsFrame.setDefaultCloseOperation(
            JFrame.HIDE_ON_CLOSE);
        visualizationsAccordionPanel = new AccordionPanel();
        visualizationsFrame.getContentPane()
            .add(visualizationsAccordionPanel);
        visualizationsFrame.setSize(1200, 600);
        visualizationsFrame.setLocation(2400, 0);
        visualizationsFrame.setVisible(true);
    }
    
    /**
     * Create the configuration- and visualization components for the
     * given {@link Module}, and show them in the respective frame
     * 
     * @param module The {@link Module}
     */
    private void createExternalizedComponents(Module module)
    {
        addModuleView(module, 
            ModuleViewTypes.CONFIGURATION_VIEW, 
            configurationsAccordionPanel,
            configurationComponentContainers);
        addModuleView(module, 
            ModuleViewTypes.VISUALIZATION_VIEW, 
            visualizationsAccordionPanel,
            visualizationComponentContainers);
    }
    
    /**
     * Add the module view with the given type for the given module to the
     * given accordion panel
     * 
     * @param module The {@link Module}
     * @param moduleViewType The {@link ModuleViewType}
     * @param accordionPanel The accordion panel
     * @param map The map that will store the component that was added to
     * the accordion
     */
    private void addModuleView(Module module, 
        ModuleViewType moduleViewType, AccordionPanel accordionPanel,
        Map<Module, JComponent> map)
    {
        ModuleCreator moduleCreator = 
            moduleCreatorRepository.get(module.getModuleInfo());
        if (moduleCreator == null)
        {
            logger.severe("No ModuleCreator found in repository "
                + "for the following ModuleInfo:");
            logger.severe(ModuleInfos.createModuleInfoString(
                module.getModuleInfo()));
            return;
        }
        ModuleView moduleView = moduleCreator.createModuleView(moduleViewType);
        if (moduleView != null)
        {
            moduleView.setModule(module);
            
            JPanel container = new JPanel(new GridLayout(1,1));
            JComponent component = (JComponent) moduleView.getComponent();
            JPanel p = new JPanel(new BorderLayout());
            p.add(new JLabel(" "), BorderLayout.NORTH);
            p.add(component, BorderLayout.CENTER);
            CloseablePanel panel = new CloseablePanel(null, p, c -> 
            {
                accordionPanel.removeFromAccordion(container);
                return true;
            });
            container.add(panel);

            accordionPanel.addToAccordion(
                module.toString(), container);
            map.put(module, container);
        }
    }

    /**
     * Set the {@link FlowEditor} for this class
     * 
     * @param flowEditor The {@link FlowEditor}
     */
    void setFlowEditor(FlowEditor flowEditor)
    {
        if (this.flowEditor != null)
        {
            Flow flow = this.flowEditor.getFlow();
            flow.removeFlowListener(flowListener);
        }
        configurationsAccordionPanel.clearAccordion();
        visualizationsAccordionPanel.clearAccordion();
        this.flowEditor = flowEditor;
        if (this.flowEditor != null)
        {
            Flow flow = this.flowEditor.getFlow();
            flow.addFlowListener(flowListener);
        }
    }

    /**
     * Create an action to externalize the configuration- and visualization
     * components of the given {@link ModuleComponent}
     * 
     * @param moduleComponent The {@link ModuleComponent}
     * @return The action
     */
    Action createExternalizeAction(ModuleComponent moduleComponent)
    {
        Action action = new AbstractAction()
        {
            /**
             * Serial UID
             */
            private static final long serialVersionUID = -847421180530850772L;

            // Initialization
            {
                putValue(NAME, "Externalize " + moduleComponent.getModule());
                putValue(SHORT_DESCRIPTION, 
                    "Show the UI components in an external window");
            }
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createExternalizedComponents(moduleComponent.getModule());
            }
        };
        return action;
    }
    

}
