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

import de.javagl.flow.module.Module;

/**
 * Interface for a {@link FlowLayout} that may be modified. All bounds
 * are assumed to be given in world coordinates.
 */
public interface MutableFlowLayout extends FlowLayout
{
    /**
     * Set the bounds for the given {@link Module}. This will store a copy 
     * of the given bounds. If the given bounds are <code>null</code>, then 
     * the layout information of the given {@link Module} will be removed.
     * 
     * @param module The {@link Module} for which the bounds will be set
     * @param bounds The bounds, or <code>null</code> to remove the 
     * layout information for the given {@link Module}
     * @return A copy of the bounds that have previously been stored for
     * the given {@link Module}, or <code>null</code> if there was no 
     * layout information associated with the given {@link Module}. 
     */
    Rectangle2D setBounds(Module module, Rectangle2D bounds);
}
