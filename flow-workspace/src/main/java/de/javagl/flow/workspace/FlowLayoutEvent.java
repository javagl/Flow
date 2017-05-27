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
import java.util.EventObject;

import de.javagl.flow.module.Module;

/**
 * An event describing a change in a {@link FlowLayout}. All bounds are
 * given in world coordinates.
 */
public final class FlowLayoutEvent extends EventObject
{
    /**
     * The {@link FlowLayout} from which this event originates
     */
    private final FlowLayout flowLayout;
    
    /**
     * The {@link Module} whose bounds have changed
     */
    private final Module module;
    
    /**
     * The old bounds that the {@link Module} had in the {@link FlowLayout}
     */
    private final Rectangle2D oldBounds;

    /**
     * The new bounds that the {@link Module} has in the {@link FlowLayout}
     */
    private final Rectangle2D newBounds;
    
    /**
     * Creates an event describing a change in the given {@link FlowLayout}.
     * Copies of the given bounds will be stored.
     * 
     * @param flowLayout The {@link FlowLayout} from which this event originates
     * @param module The {@link Module} whose bounds have changed
     * @param oldBounds The old bounds of the {@link Module}. May be 
     * <code>null</code> of the {@link Module} was just added to 
     * the {@link FlowLayout}.
     * @param newBounds The new bounds of the {@link Module}. May be 
     * <code>null</code> if the {@link Module} was just removed from 
     * the {@link FlowLayout}.
     */
    public FlowLayoutEvent(
        FlowLayout flowLayout,
        Module module, 
        Rectangle2D oldBounds,
        Rectangle2D newBounds)
   {
        super(flowLayout);
        this.flowLayout = flowLayout;
        this.module = module;
        this.oldBounds = GeomUtils.copy(oldBounds);
        this.newBounds = GeomUtils.copy(newBounds);
    }
    
    /**
     * Returns the {@link FlowLayout} from which this event originates 
     * 
     * @return The {@link FlowLayout} from which this event originates 
     */
    public FlowLayout getFlowLayout()
    {
        return flowLayout;
    }
    
    /**
     * Returns the {@link Module} whose bounds have changed 
     * 
     * @return The {@link Module} whose bounds have changed
     */
    public Module getModule()
    {
        return module;
    }
    
    /**
     * Returns a copy of the old bounds that the {@link Module} had in 
     * the {@link FlowLayout}, before this event happened. 
     * If the {@link Module} did not have any layout information, 
     * then <code>null</code> is returned. 
     * 
     * @return The old bounds of the {@link Module}. May be <code>null</code>.
     */
    public Rectangle2D getOldBounds()
    {
        return GeomUtils.copy(oldBounds);
    }
    
    /**
     * Returns a copy of the new bounds that the {@link Module} has in 
     * the {@link FlowLayout}, after this event happened. 
     * If the {@link Module} was removed from the layout, then 
     * <code>null</code> is returned. 
     * 
     * @return The new bounds of the {@link Module}. May be <code>null</code>.
     */
    public Rectangle2D getNewBounds()
    {
        return GeomUtils.copy(newBounds);
    }
    
}
