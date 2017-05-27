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

package de.javagl.flow.workspace;

import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javagl.flow.module.Module;

/**
 * Default implementation of a {@link MutableFlowLayout}. All bounds
 * are given in world coordinates.
 */
class DefaultFlowLayout implements MutableFlowLayout
{
    /**
     * The map that stores the bounds of each module. 
     */
    private final Map<Module, Rectangle2D> map;
    
    /**
     * The {@link FlowLayoutListener} instances that want to be informed about
     * changes in the layout 
     */
    private final List<FlowLayoutListener> flowLayoutListeners;

    /**
     * Default constructor
     */
    DefaultFlowLayout()
    {
        this.map = new LinkedHashMap<Module, Rectangle2D>();
        this.flowLayoutListeners =
            new CopyOnWriteArrayList<FlowLayoutListener>();
    }

    /**
     * Copy constructor
     * 
     * @param other The {@link FlowLayout} to copy
     */
    DefaultFlowLayout(FlowLayout other)
    {
        this();
        map.putAll(other.getEntries());
    }
    
    @Override
    public Rectangle2D getBounds(Module module)
    {
        return GeomUtils.copy(map.get(module));
    }
    
    @Override
    public Map<Module, Rectangle2D> getEntries()
    {
        return Collections.unmodifiableMap(GeomUtils.copyValues(map));
    }

    @Override
    public Rectangle2D setBounds(Module module, Rectangle2D bounds)
    {
        Rectangle2D oldBounds = null;
        Rectangle2D newBounds = GeomUtils.copy(bounds);
        if (newBounds == null)
        {
            oldBounds = GeomUtils.copy(map.remove(module));
        }
        else
        {
            oldBounds = GeomUtils.copy(map.put(module, newBounds));
        }
        if (!Objects.equals(oldBounds, newBounds))
        {
            fireFlowLayoutChanged(module, oldBounds, newBounds);
        }
        return oldBounds;
    }
    
    /**
     * Notify each registered {@link FlowLayoutListener} about a change
     * in the layout.
     * 
     * @param module The {@link Module} which is affected by the layout change
     * @param oldBounds The old bounds of the {@link Module}, or 
     * <code>null</code> if the {@link Module} was not yet contained 
     * in this layout.
     * @param newBounds The new bounds of the {@link Module}, or 
     * <code>null</code> if the {@link Module} was removed from 
     * this layout.
     */
    protected final void fireFlowLayoutChanged(
        Module module, Rectangle2D oldBounds, Rectangle2D newBounds)
    {
        if (!flowLayoutListeners.isEmpty())
        {
            FlowLayoutEvent flowLayoutEvent = 
                new FlowLayoutEvent(this, module, oldBounds, newBounds);
            for (FlowLayoutListener flowLayoutListener : 
                 flowLayoutListeners)
            {
                flowLayoutListener.flowLayoutChanged(flowLayoutEvent);
            }
        }
    }

    @Override
    public void addFlowLayoutListener(
        FlowLayoutListener flowLayoutListener)
    {
        flowLayoutListeners.add(flowLayoutListener);
    }

    @Override
    public void removeFlowLayoutListener(
        FlowLayoutListener flowLayoutListener)
    {
        flowLayoutListeners.remove(flowLayoutListener);
    }

    
}
