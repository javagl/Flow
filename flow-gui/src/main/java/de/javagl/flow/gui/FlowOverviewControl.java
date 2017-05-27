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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

/**
 * The mouse- and mouse motion listener that will be attached to a 
 * {@link FlowOverview} and center the {@link FlowEditorComponent} inside a 
 * {@link FlowEditorViewport} according to the position of the mouse in the 
 * overview. Therefore, when the mouse is pressed or dragged, the coordinates 
 * of the mouse will be translated into "world" coordinates of the 
 * {@link FlowEditorComponent}, and the respective point will be centered in 
 * the {@link FlowEditorViewport} by updating the bounds of the 
 * {@link FlowEditorComponent}.
 */
final class FlowOverviewControl extends MouseAdapter
    implements MouseListener, MouseMotionListener
{
    /**
     * The {@link FlowOverview} to which this control is attached
     */
    private final FlowOverview flowOverview;
    
    /**
     * The {@link FlowEditorViewport} which contains the 
     * {@link FlowEditorComponent}. 
     */
    private final FlowEditorViewport flowEditorViewport;
    
    /**
     * Creates a new control for the given {@link FlowOverview} 
     * and {@link FlowEditorViewport}
     * 
     * @param flowOverview The {@link FlowOverview}
     * @param flowEditorViewport The {@link FlowEditorViewport}
     */
    FlowOverviewControl(
        FlowOverview flowOverview,
        FlowEditorViewport flowEditorViewport)
    {
        this.flowOverview = flowOverview;
        this.flowEditorViewport = flowEditorViewport;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Point localPoint = e.getPoint();
        centerAt(localPoint);
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        Point localPoint = e.getPoint();
        centerAt(localPoint);
    }
    
    /**
     * Center the view at the given mouse position
     * 
     * @param localPoint The local mouse position
     */
    private void centerAt(Point localPoint)
    {
        Point2D world = flowOverview.screenToWorld(localPoint, null);
        FlowEditorComponent flowEditorComponent = 
            flowEditorViewport.getFlowEditorComponent();
        Rectangle bounds = flowEditorComponent.getBounds();
        bounds.x = (int) (-bounds.width / 2 -
            (world.getX() - bounds.width / 2) + 
            flowEditorViewport.getWidth() / 2);
        bounds.y = (int)(-bounds.height / 2 - 
            (world.getY() - bounds.height / 2) + 
            flowEditorViewport.getHeight() / 2);
        flowEditorComponent.setBounds(bounds);
        flowOverview.repaint();
    }
    
}


