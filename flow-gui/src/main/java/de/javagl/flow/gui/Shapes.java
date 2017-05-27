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
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods related to shapes
 */
class Shapes
{
    /**
     * Create a list containing line segments that approximate the given 
     * shape.
     * 
     * NOTE: Copied from https://github.com/javagl/Geom/blob/master/
     *     src/main/java/de/javagl/geom/Shapes.java
     * 
     * @param shape The shape
     * @param flatness The allowed flatness
     * @return The list of line segments
     */
    public static List<Line2D> computeLineSegments(
        Shape shape, double flatness)
    {
        List<Line2D> result = new ArrayList<Line2D>();
        PathIterator pi = shape.getPathIterator(null, flatness);
        double[] coords = new double[6];
        double previous[] = new double[2];
        double first[] = new double[2];
        while (!pi.isDone())
        {
            int segment = pi.currentSegment(coords);
            switch (segment)
            {
                case PathIterator.SEG_MOVETO:
                    previous[0] = coords[0];
                    previous[1] = coords[1];
                    first[0] = coords[0];
                    first[1] = coords[1];
                    break;

                case PathIterator.SEG_CLOSE:
                    result.add(new Line2D.Double(
                        previous[0], previous[1],
                        first[0], first[1]));
                    previous[0] = first[0];
                    previous[1] = first[1];
                    break;

                case PathIterator.SEG_LINETO:
                    result.add(new Line2D.Double(
                        previous[0], previous[1],
                        coords[0], coords[1]));
                    previous[0] = coords[0];
                    previous[1] = coords[1];
                    break;

                case PathIterator.SEG_QUADTO:
                    // Should never occur
                    throw new AssertionError(
                        "SEG_QUADTO in flattened path!");

                case PathIterator.SEG_CUBICTO:
                    // Should never occur
                    throw new AssertionError(
                        "SEG_CUBICTO in flattened path!");

                default:
                    // Should never occur
                    throw new AssertionError(
                        "Invalid segment in flattened path!");
            }
            pi.next();
        }
        return result;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Shapes()
    {
        // Private constructor to prevent instantiation
    }
}
