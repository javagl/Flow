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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.JPanel;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewTypes;
import de.javagl.flow.repository.Repository;

/**
 * The panel that is on the JLayeredPane.DRAG_LAYER of a
 * {@link FlowEditorViewport}. It contains the selection
 * rectangle and the preview of dropped modules
 */
final class DragPanel extends JPanel
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(DragPanel.class.getName());
    
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 6999927992247219765L;

    /**
     * The current selection rectangle
     */
    private Rectangle2D selectionRectangle;

    /**
     * The {@link Module} of the current drop preview
     */
    private Module currentDropPreviewModule = null;
    
    /**
     * The component that is currently shown as a drop preview
     */
    private ModuleComponent currentDropPreviewComponent = null;
    
    /**
     * The {@link Repository} that will provide the {@link ModuleCreator}
     * for a {@link Module}, to create the {@link ModuleView} instances.
     */
    private final Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository;
    
    /**
     * Default constructor
     * @param moduleCreatorRepository The {@link Repository} of 
     * {@link ModuleCreator} objects
     */
    DragPanel(Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository)
    {
        super(null);
        this.moduleCreatorRepository = moduleCreatorRepository;
        setOpaque(false);
        setFocusable(true);
    }
    
    /**
     * Set the given selection rectangle. May be <code>null</code>.
     * 
     * @param rectangle The selection rectangle
     */
    void setSelectionRectangle(Rectangle2D rectangle)
    {
        this.selectionRectangle = rectangle;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics gr)
    {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D)gr;
        
        if (selectionRectangle != null)
        {
            BasicStroke dashed =
                new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    1.0f, new float[]{3.0f}, 0.0f);
            g.setStroke(dashed);
            g.setColor(Color.RED);
            g.draw(selectionRectangle);
        }
    }

    /**
     * Set the drop preview for the given {@link Module}. Subsequent calls 
     * with a {@link Module} that is equal to the previous one will only 
     * update the preview position. A call with a <code>null</code> 
     * {@link Module} will remove the preview.  
     * 
     * @param module The {@link Module}
     * @param location The preview position
     */
    void setDropPreviewFor(Module module, Point location)
    {
        if (module == null)
        {
            if (currentDropPreviewComponent != null)
            {
                // Set the module to null, to make sure that it is detached
                // from the preview component (i.e. that all listeners
                // that the component may have added to the module are
                // actually removed)
                currentDropPreviewComponent.setModule(null);
                remove(currentDropPreviewComponent);
                validate();
                repaint();
            }
            currentDropPreviewModule = null;
            currentDropPreviewComponent = null;
        }
        else
        {
            if (module.equals(currentDropPreviewModule))
            {
                Dimension size = currentDropPreviewComponent.getSize();
                currentDropPreviewComponent.setLocation(
                    location.x - size.width / 2, 
                    location.y - size.height / 2);
                repaint();
            }
            else
            {
                if (currentDropPreviewComponent != null)
                {
                    remove(currentDropPreviewComponent);
                }
                
                // TODO: Avoid code duplication from 
                // FlowEditorComponent#createModuleComponent
                ModuleCreator moduleCreator = 
                    moduleCreatorRepository.get(module.getModuleInfo());

                // TODO Currently, only the two default module view 
                // types are supported here
                ModuleView configurationView = null; 
                ModuleView visualizationView = null;
                if (moduleCreator == null)
                {
                    logger.severe("No ModuleCreator found in repository "
                        + "for the following ModuleInfo:");
                    logger.severe(ModuleInfos.createModuleInfoString(
                        module.getModuleInfo()));
                }
                else
                {
                    configurationView = moduleCreator.createModuleView(
                        ModuleViewTypes.CONFIGURATION_VIEW); 
                    visualizationView = moduleCreator.createModuleView(
                        ModuleViewTypes.VISUALIZATION_VIEW); 
                }
                    
                ModuleComponent moduleComponent = 
                    new ModuleComponent(configurationView, visualizationView);
                moduleComponent.setModule(module);
                
                Dimension size = moduleComponent.getPreferredSize();
                moduleComponent.setSize(size);
                moduleComponent.setLocation(
                    location.x - size.width / 2, 
                    location.y - size.height / 2);
                moduleComponent.setShadeColor(new Color(190,255,190,190));
                GuiUtils.disableDropTargetsDeep(moduleComponent);
                
                currentDropPreviewModule = module;
                currentDropPreviewComponent = moduleComponent;
                add(currentDropPreviewComponent);
                
                validate();
                repaint();
            }
        }
    }
    
}
