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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

/**
 * A panel that acts as a special viewport for a {@link FlowEditorComponent}. 
 * It shows a part of the {@link FlowEditorComponent}, and takes care that  
 * a maximized {@link ModuleComponent} that is shown in the 
 * {@link FlowEditorComponent} stays as large as the viewport.
 */
final class FlowEditorViewport extends JPanel 
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 5476579551846862328L;

    /**
     * The {@link FlowEditorComponent}
     */
    private final FlowEditorComponent flowEditorComponent;
    
    /**
     * Creates a new FlowEditorViewport
     * 
     * @param flowEditorComponent The {@link FlowEditorComponent}
     * to show in this viewport 
     */
    FlowEditorViewport(
        final FlowEditorComponent flowEditorComponent)
    {
        this.flowEditorComponent = flowEditorComponent;
        setLayout(null);
        setBackground(Color.RED);
        
        // When this viewport is resized, then any maximized
        // module component should be resized accordingly
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                flowEditorComponent.
                    updateMaximizedModuleComponentBounds();
            }
        });
        flowEditorComponent.setBounds(
            -Short.MAX_VALUE / 2, -Short.MAX_VALUE / 2, 
            Short.MAX_VALUE, Short.MAX_VALUE);
        add(flowEditorComponent);
    }
    

    /**
     * Returns the {@link FlowEditorComponent} that is shown
     * in this viewport
     * 
     * @return The {@link FlowEditorComponent}
     */
    FlowEditorComponent getFlowEditorComponent()
    {
        return flowEditorComponent;
    }
    
}

