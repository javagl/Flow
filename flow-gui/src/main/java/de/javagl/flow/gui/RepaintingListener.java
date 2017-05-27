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

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * A listener that will repaint a given component upon each event. 
 * Used for triggering a repaint of the {@link FlowOverview} 
 * due to mouse- or other actions in the {@link FlowEditorComponent}.
 * (Not very elegant, but OK...)
 */
final class RepaintingListener implements 
    MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener
{
    /**
     * The component that will be repainted
     */
    private final Component component;
    
    /**
     * Creates a listener that will repaint the given component
     * on each event
     * 
     * @param component The component to repaint
     */
    RepaintingListener(Component component)
    {
        this.component = component;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        component.repaint();        
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        component.repaint();        
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        component.repaint();        
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        component.repaint();        
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        component.repaint();        
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        component.repaint();        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        component.repaint();        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        component.repaint();        
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        component.repaint();        
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        component.repaint();        
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        component.repaint();        
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
        component.repaint();        
    }

}