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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A panel that paints an overview of the {@link FlowEditorComponent} that
 * is shown in a {@link FlowEditorViewport}. It will paint the area that is 
 * covered by {@link ModuleComponent} objects in the 
 * {@link FlowEditorComponent}, scaled and translated to fill this component.
 */
final class FlowOverview extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -405977797988254545L;
    
    /**
     * The {@link FlowEditorViewport} that is painted
     */
    private final FlowEditorViewport flowEditorViewport;
    
    /**
     * The current scaling factor that is applied in order
     * to paint the whole {@link FlowEditorComponent} into 
     * this panel
     */
    private float scaling = 1.0f;
    
    /**
     * The current offset in X-direction, describing the 
     * upper left point of the area of the 
     * {@link FlowEditorComponent} that is painted
     */
    private float offsetX = 0.0f;

    /**
     * The current offset in Y-direction, describing the 
     * upper left point of the area of the 
     * {@link FlowEditorComponent} that is painted
     */
    private float offsetY = 0.0f;
    
    /**
     * Creates a new overview for the given {@link FlowEditorViewport}
     * 
     * @param flowEditorViewport The {@link FlowEditorViewport}
     */
    FlowOverview(FlowEditorViewport flowEditorViewport)
    {
        this.flowEditorViewport = flowEditorViewport;
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
    
    @Override
    protected void paintComponent(Graphics gr)
    {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D)gr;
        
        FlowEditorComponent flowEditorComponent =
            flowEditorViewport.getFlowEditorComponent();
        Rectangle visibleArea = computeVisibleArea();
        float scaleX = (float)getWidth() / visibleArea.width;
        float scaleY = (float)getHeight() / visibleArea.height;
        scaling = Math.min(scaleX, scaleY);
        offsetX = -visibleArea.x;
        offsetY = -visibleArea.y;
        
        // Paint the relevant area of the FlowEditorComponent
        // into the graphics
        Graphics2D temp = (Graphics2D)g.create();
        temp.scale(scaling, scaling);
        temp.translate(offsetX, offsetY);
        try
        {
            flowEditorComponent.print(temp);
        }
        finally
        {
            temp.dispose();
        }

        // Paint a rectangle indicating the current
        // viewport area of the FlowEditorComponent
        Rectangle bounds = flowEditorComponent.getBounds();
        int x0 = -bounds.x; 
        int y0 = -bounds.y;
        Rectangle viewRectangle = 
            new Rectangle(x0, y0, 
                flowEditorViewport.getWidth(), 
                flowEditorViewport.getHeight());
        Rectangle2D viewArea = worldToScreen(viewRectangle, null);
        g.setColor(new Color(0, 128, 0));
        g.draw(viewArea);
    }
    
    
    /**
     * Returns the area of the shown component that should be visible
     * 
     * @return The area of the shown component that should be visible
     */
    private Rectangle computeVisibleArea()
    {
        FlowEditorComponent flowEditorComponent =
            flowEditorViewport.getFlowEditorComponent();
        Rectangle visibleArea = 
            flowEditorComponent.computeModuleComponentBounds();
        return visibleArea;
    }
    
    
    
    /**
     * Convert the given world coordinates into screen coordinates
     * 
     * @param x The world coordinates
     * @return The screen coordinates
     */
    private double worldToScreenX(double x)
    {
        return (x + offsetX) * scaling;
    }

    /**
     * Convert the given world coordinates into screen coordinates
     * 
     * @param y The world coordinates
     * @return The screen coordinates
     */
    private double worldToScreenY(double y)
    {
        return (y + offsetY) * scaling;
    }
    
    /**
     * Convert the given screen coordinates into world coordinates
     * 
     * @param x The screen coordinates
     * @return The world coordinates
     */
    private double screenToWorldX(double x)
    {
        return x / scaling - offsetX;
    }
    
    /**
     * Convert the given screen coordinates into world coordinates
     * 
     * @param y The screen coordinates
     * @return The world coordinates
     */
    private double screenToWorldY(double y)
    {
        return y / scaling - offsetY;
    }
    
    /**
     * Convert the given point from screen coordinates into
     * world coordinates. If the given destination point is
     * <code>null</code>, a new point will be created.
     * 
     * @param src The source point
     * @param dst The destination point
     * @return The destination point 
     */
    Point2D screenToWorld(Point2D src, Point2D dst)
    {
        double x = screenToWorldX(src.getX());
        double y = screenToWorldY(src.getY());
        if (dst == null)
        {
            dst = new Point2D.Double(x, y);
        }
        else
        {
            dst.setLocation(x, y);
        }
        return dst;
    }

    
    /**
     * Convert the given rectangle from world coordinates into
     * screen coordinates. If the given destination rectangle is
     * <code>null</code>, a new rectangle will be created.
     * 
     * @param src The source rectangle
     * @param dst The destination rectangle
     * @return The destination rectangle 
     */
    private Rectangle2D worldToScreen(Rectangle2D src, Rectangle2D dst)
    {
        double x0 = src.getMinX();
        double y0 = src.getMinY();
        double x1 = src.getMaxX();
        double y1 = src.getMaxY();
        double tx0 = worldToScreenX(x0);
        double ty0 = worldToScreenY(y0);
        double tx1 = worldToScreenX(x1);
        double ty1 = worldToScreenY(y1);
        if (dst == null)
        {
            dst = new Rectangle2D.Double(tx0, ty0, tx1 - tx0, ty1 - ty0);
        }
        else
        {
            dst.setRect(tx0, ty0, tx1 - tx0, ty1 - ty0);
        }
        return dst;
    }
    
}
