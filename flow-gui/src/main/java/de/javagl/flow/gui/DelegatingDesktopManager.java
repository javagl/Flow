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

import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

/**
 * Implementation of a {@link DesktopManager} that forwards all method calls 
 * to a delegate. Serving as a stub implementation for own (delegating) 
 * DesktopManagers where certain methods may be overridden selectively. 
 */
abstract class DelegatingDesktopManager implements DesktopManager
{
    /**
     * The delegate
     */
    private final DesktopManager desktopManager;

    /**
     * Create a new {@link DelegatingDesktopManager} with the
     * given delegate
     * 
     * @param desktopManager The delegate
     */
    protected DelegatingDesktopManager(DesktopManager desktopManager)
    {
        this.desktopManager = desktopManager;
    }
    
    @Override
    public void setBoundsForFrame(JComponent f, int newX, int newY,
        int newWidth, int newHeight)
    {
        desktopManager.setBoundsForFrame(f, newX, newY, newWidth, newHeight);
    }

    @Override
    public void resizeFrame(JComponent f, int newX, int newY, int newWidth,
        int newHeight)
    {
        desktopManager.resizeFrame(f, newX, newY, newWidth, newHeight);
    }

    @Override
    public void openFrame(JInternalFrame f)
    {
        desktopManager.openFrame(f);
    }

    @Override
    public void minimizeFrame(JInternalFrame f)
    {
        desktopManager.minimizeFrame(f);
    }

    @Override
    public void maximizeFrame(JInternalFrame f)
    {
        desktopManager.maximizeFrame(f);
    }

    @Override
    public void iconifyFrame(JInternalFrame f)
    {
        desktopManager.iconifyFrame(f);
    }

    @Override
    public void endResizingFrame(JComponent f)
    {
        desktopManager.endResizingFrame(f);
    }

    @Override
    public void endDraggingFrame(JComponent f)
    {
        desktopManager.endDraggingFrame(f);
    }

    @Override
    public void dragFrame(JComponent f, int newX, int newY)
    {
        desktopManager.dragFrame(f, newX, newY);
    }

    @Override
    public void deiconifyFrame(JInternalFrame f)
    {
        desktopManager.deiconifyFrame(f);
    }

    @Override
    public void deactivateFrame(JInternalFrame f)
    {
        desktopManager.deactivateFrame(f);
    }

    @Override
    public void closeFrame(JInternalFrame f)
    {
        desktopManager.closeFrame(f);
    }

    @Override
    public void beginResizingFrame(JComponent f, int direction)
    {
        desktopManager.beginResizingFrame(f, direction);
    }

    @Override
    public void beginDraggingFrame(JComponent f)
    {
        desktopManager.beginDraggingFrame(f);
    }

    @Override
    public void activateFrame(JInternalFrame f)
    {
        desktopManager.activateFrame(f);
    }
}