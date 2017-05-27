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

import java.awt.Shape;
import java.awt.geom.Point2D;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * An interface for classes that may provide information for painting the 
 * shapes that represent {@link Link} objects. It may provide the shape 
 * for the whole link, as well as the start- and end points of links
 * based on their {@link InputSlot} and {@link OutputSlot} objects. 
 * 
 * NOTE: This interface is part of an abstraction that is still under
 * construction. It may be omitted in the future.
 * Also see {@link ModuleComponentOwner}
 */
interface LinkShapeProvider
{
    /**
     * Returns the center position of the given {@link OutputSlot}, in world 
     * coordinates
     * 
     * @param slot The slot
     * @return The center position of the {@link OutputSlot}
     */
    Point2D centerOfOutput(OutputSlot slot);

    /**
     * Returns the center position of the given {@link InputSlot}, in world 
     * coordinates
     * 
     * @param slot The slot
     * @return The center position of the {@link InputSlot}
     */
    Point2D centerOfInput(InputSlot slot);
    
    /**
     * Returns the shape that should be painted for the given {@link Link},
     * in world coordinates.
     * 
     * @param link The {@link Link}
     * @return The shape
     */
    Shape shapeForLink(Link link);
}
