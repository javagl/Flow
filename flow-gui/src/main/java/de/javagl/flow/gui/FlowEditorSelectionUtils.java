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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * Utility methods for selection handling in a {@link FlowEditor}
 * via a {@link FlowEditorComponent}
 */
class FlowEditorSelectionUtils
{
    /**
     * Update the selection of {@link Module} objects and {@link Link} objects 
     * for a click that is described by the given event
     *
     * @param flowEditorComponent The {@link FlowEditorComponent}
     * @param flowEditor The {@link FlowEditor}
     * @param e The mouse event
     */
    static void updateSelectionForClickAt(
        FlowEditorComponent flowEditorComponent,
        FlowEditor flowEditor,
        MouseEvent e)
    {
        ModuleComponent clickedModuleComponent = 
            flowEditorComponent.getModuleComponentAt(e.getPoint());
        if (clickedModuleComponent != null)
        {
            updateSelectionForClickAt(flowEditor, clickedModuleComponent, e);
        }
        else
        {
            Link clickedLink = 
                flowEditorComponent.getLinkAt(e.getPoint());
            if (clickedLink != null)
            {
                updateSelectionForClickAt(flowEditor, clickedLink, e);
            }
        }
    }

    
    /**
     * Update the selection of {@link Link} objects for a click at the given 
     * {@link Link}. 
     * 
     * @param flowEditor The {@link FlowEditor}
     * @param clickedLink The clicked {@link Link}
     * @param e The mouse event
     */
    private static void updateSelectionForClickAt(
        FlowEditor flowEditor, Link clickedLink, MouseEvent e)
    {
        Set<Module> modulesAdded = new LinkedHashSet<Module>();
        Set<Module> modulesRemoved = new LinkedHashSet<Module>();
        
        Set<Link> linksAdded = new LinkedHashSet<Link>();
        Set<Link> linksRemoved = new LinkedHashSet<Link>();

        Set<Module> selectedModules = flowEditor.getSelectedModules();
        Set<Link> selectedLinks = flowEditor.getSelectedLinks();

        if (selectedLinks.contains(clickedLink))
        {
            if (e.isControlDown())
            {
                linksRemoved.add(clickedLink);
            }
            else if (e.isShiftDown())
            {
                linksRemoved.add(clickedLink);
            }
            else if (selectedLinks.size() == 1 && selectedModules.isEmpty())
            {
                linksRemoved.add(clickedLink);
            }
            else
            {
                linksRemoved.addAll(selectedLinks);
                linksRemoved.remove(clickedLink);
                linksAdded.add(clickedLink);
                
                modulesRemoved.addAll(selectedModules);
            }
        }
        else if (e.isControlDown()) 
        {
            linksAdded.add(clickedLink);
        }
        else
        {
            linksRemoved.addAll(selectedLinks);
            linksAdded.add(clickedLink);

            modulesRemoved.addAll(selectedModules);
        }

        flowEditor.changeSelection(
            modulesAdded, modulesRemoved, 
            linksAdded, linksRemoved);
    }

    
    /**
     * Update the selection of {@link Module} objects for a click at the given 
     * {@link Link}. 
     * 
     * @param flowEditor The {@link FlowEditor}
     * @param clickedModuleComponent The clicked {@link ModuleComponent}
     * @param e The mouse event
     */
    private static void updateSelectionForClickAt(
        FlowEditor flowEditor, ModuleComponent clickedModuleComponent, 
        MouseEvent e)
    {
        Module clickedModule = clickedModuleComponent.getModule();

        Set<Module> modulesAdded = new LinkedHashSet<Module>();
        Set<Module> modulesRemoved = new LinkedHashSet<Module>();

        Set<Link> linksAdded = new LinkedHashSet<Link>();
        Set<Link> linksRemoved = new LinkedHashSet<Link>();
        
        Set<Module> selectedModules = flowEditor.getSelectedModules();
        Set<Link> selectedLinks = flowEditor.getSelectedLinks();
        
        if (selectedModules.contains(clickedModule))
        {
            if (e.isControlDown())
            {
                modulesRemoved.add(clickedModule);
            }
            else if (e.isShiftDown())
            {
                modulesRemoved.add(clickedModule);
            }
            else if (selectedModules.size() == 1 && selectedLinks.isEmpty())
            {
                modulesRemoved.add(clickedModule);
            }
            else
            {
                modulesRemoved.addAll(selectedModules);
                modulesRemoved.remove(clickedModule);
                modulesAdded.add(clickedModule);
                
                linksRemoved.addAll(selectedLinks);
            }
        } 
        else if (e.isControlDown()) 
        {
            modulesAdded.add(clickedModule);
        }
        else
        {
            modulesRemoved.addAll(selectedModules);
            modulesAdded.add(clickedModule);

            linksRemoved.addAll(selectedLinks);
        }
        
        modulesAdded.removeAll(selectedModules);
        modulesRemoved.retainAll(selectedModules);
        linksAdded.removeAll(selectedLinks);
        linksRemoved.retainAll(selectedLinks);
        flowEditor.changeSelection(
            modulesAdded, modulesRemoved, 
            linksAdded, linksRemoved);
    }

    
    /**
     * Update the selection of {@link Module} objects and {@link Link} objects 
     * for for the given selection rectangle that was finished with the
     * given mouse event
     *
     * @param flowEditorComponent The {@link FlowEditorComponent}
     * @param flowEditor The {@link FlowEditor}
     * @param rectangle The selection rectangle
     * @param e The mouse event
     */
    static void updateSelectionFromRectangle(
        FlowEditorComponent flowEditorComponent, FlowEditor flowEditor,
        Rectangle rectangle, MouseEvent e)
    {
        Set<Module> modulesInRectangle = new HashSet<Module>();
        for (ModuleComponent moduleComponent : 
             flowEditorComponent.getModuleComponents())
        {
            if (rectangle.intersects(moduleComponent.getBounds()))
            {
                Module module = moduleComponent.getModule();
                modulesInRectangle.add(module);
            }
        }
        Map<Link, Shape> linkShapes = 
            flowEditorComponent.getLinkShapes();
        Set<Link> linksInRectangle = 
            computeIntersectedElements(linkShapes, rectangle);
        
        Set<Module> modulesAdded = new LinkedHashSet<Module>();
        Set<Module> modulesRemoved = new LinkedHashSet<Module>();

        Set<Link> linksAdded = new LinkedHashSet<Link>();
        Set<Link> linksRemoved = new LinkedHashSet<Link>();
        
        Set<Module> selectedModules = flowEditor.getSelectedModules();
        Set<Link> selectedLinks = flowEditor.getSelectedLinks();

        if (e.isControlDown())
        {
            modulesAdded.addAll(modulesInRectangle);
            linksAdded.addAll(linksInRectangle);
        }
        else if (e.isShiftDown())
        {
            modulesRemoved.addAll(modulesInRectangle);
            linksRemoved.addAll(linksInRectangle);
        }
        else
        {
            modulesRemoved.addAll(selectedModules);
            modulesRemoved.removeAll(modulesInRectangle);
            modulesAdded.addAll(modulesInRectangle);
            
            linksRemoved.addAll(selectedLinks);
            linksRemoved.removeAll(linksInRectangle);
            linksAdded.addAll(linksInRectangle);
        }

        flowEditor.changeSelection(
            modulesAdded, modulesRemoved, 
            linksAdded, linksRemoved);
    }
    
    
    /**
     * Compute all keys from the given map whose value (shape) intersects
     * the given rectangle, using the {@link #intersects(Rectangle2D, Shape)}
     * method. 
     * 
     * @param <T> The type of the keys
     * @param map The map
     * @param rectangle The rectangle
     * @return The keys whose values intersect the given rectangle
     */
    private static <T> Set<T> computeIntersectedElements(
        Map<T, Shape> map, Rectangle2D rectangle)
    {
        Set<T> result = new LinkedHashSet<T>();
        for (Entry<T, Shape> entry : map.entrySet())
        {
            T key = entry.getKey();
            Shape shape = entry.getValue();
            if (intersects(rectangle, shape.getBounds2D()))
            {
                result.add(key);
            }
        }
        return result;
    }
    
    /**
     * Returns whether the given rectangle intersects the given shape.
     * This will be checked by a bounding box intersection test, and
     * (if the bounding boxes intersect) by checking whether any line
     * segment of the given shape intersects the rectangle.
     * 
     * @param rectangle The rectangle
     * @param shape The shape
     * @return Whether the rectangle and the shape intersect
     */
    private static boolean intersects(Rectangle2D rectangle, Shape shape)
    {
        if (rectangle.intersects(shape.getBounds2D()))
        {
            List<Line2D> lines = Shapes.computeLineSegments(shape, 0.5);
            for (int i=0; i<lines.size(); i++)
            {
                Line2D line = lines.get(i);
                if (line.intersects(rectangle))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private FlowEditorSelectionUtils()
    {
        // Private constructor to prevent instantiation
    }


}
