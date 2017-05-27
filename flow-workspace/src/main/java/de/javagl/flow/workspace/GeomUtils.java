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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility methods related to classes from <code>java.awt.geom</code>.<br>
 * <br>
 * <b>This class should not be considered as part of the public API!</b><br>
 */
public class GeomUtils
{
    
    /**
     * Creates a copy of the given map, where the values (rectangles)
     * are copied
     * 
     * @param <T> The key type
     * @param map The map
     * @return The copy of the map
     */
    public static <T> Map<T, Rectangle2D> copyValues(Map<T, Rectangle2D> map)
    {
        Map<T, Rectangle2D> result = 
            new LinkedHashMap<T, Rectangle2D>();
        for (Entry<T, Rectangle2D> entry : map.entrySet())
        {
            T key = entry.getKey();
            Rectangle2D bounds = entry.getValue();
            result.put(key, copy(bounds));
        }
        return result;    
    }
    
    /**
     * Returns a copy of the given rectangle, or <code>null</code> if
     * the given rectangle is <code>null</code>
     * 
     * @param rectangle The rectangle
     * @return The copy
     */
    public static Rectangle2D copy(Rectangle2D rectangle)
    {
        if (rectangle == null)
        {
            return null;
        }
        return new Rectangle2D.Double(
            rectangle.getX(), rectangle.getY(), 
            rectangle.getWidth(), rectangle.getHeight());
    }

    /**
     * Returns a rectangle with the same size as the given one, but with
     * its center at the given position
     * 
     * @param rectangle The rectangle
     * @param center The center 
     * @return The copy
     */
    public static Rectangle2D moveTo(Rectangle2D rectangle, Point2D center)
    {
        return new Rectangle2D.Double(
            center.getX() - rectangle.getWidth() * 0.5, 
            center.getY() - rectangle.getHeight() * 0.5, 
            rectangle.getWidth(), 
            rectangle.getHeight());
    }

    
    /**
     * Private constructor to prevent instantiation
     */
    GeomUtils()
    {
        // Private constructor to prevent instantiation
    }

}
