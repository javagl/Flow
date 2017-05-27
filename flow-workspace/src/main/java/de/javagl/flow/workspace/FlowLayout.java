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
import java.util.Map;

import de.javagl.flow.Flow;
import de.javagl.flow.module.Module;

// TODO FlowLayout - Store "maximized" state info?

/**
 * Interface for classes that describe the layout of {@link Module} objects
 * inside a {@link Flow}. The bounds of the {@link Module} objects are always
 * considered to be in world coordinates.
 */
public interface FlowLayout
{
    /**
     * Returns a copy of the bounds of the given {@link Module}. Returns 
     * <code>null</code> if the given {@link Module} is not contained 
     * in this layout.
     * 
     * @param module The {@link Module}
     * @return A copy of the bounds of the {@link Module}
     */
    Rectangle2D getBounds(Module module);

    /**
     * Returns an unmodifiable copy of the entries in this layout.
     * The bounds will also be copies of the bounds that are stored
     * in this layout. 
     * 
     * @return The entries in this layout
     */
    Map<Module, Rectangle2D> getEntries();
    
    /**
     * Add the given {@link FlowLayoutListener} to be informed about
     * changes in this layout
     * 
     * @param flowLayoutListener The {@link FlowLayoutListener} to add
     */
    void addFlowLayoutListener(FlowLayoutListener flowLayoutListener);
    
    /**
     * Remove the given {@link FlowLayoutListener}
     * 
     * @param flowLayoutListener The {@link FlowLayoutListener} to remove
     */
    void removeFlowLayoutListener(FlowLayoutListener flowLayoutListener);
    
}
