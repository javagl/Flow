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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.javagl.flow.Flow;

/**
 * A panel for a {@link FlowExecutorControl}
 */
class FlowExecutorControlPanel extends JPanel
{
    /**
     * The current {@link FlowExecutorControl}
     */
    private FlowExecutorControl flowExecutorControl;
    
    /**
     * The button to execute the {@link Flow}
     */
    private JButton executeFlowButton;
    
    /**
     * The button to execute the {@link Flow}
     */
    private JButton executeFlowResponsiveButton;
    
    /**
     * The button to cancel an ongoing execution
     */
    private JButton cancelButton;

    /**
     * Default constructor
     */
    FlowExecutorControlPanel()
    {
        super(new BorderLayout());
        
        add(createControlPanel(), BorderLayout.CENTER);
    }

    /**
     * Create the control panel
     * 
     * @return The control panel
     */
    private JPanel createControlPanel()
    {
        JPanel controlPanel = new JPanel();
        
        executeFlowButton = new JButton("Execute flow");
        executeFlowButton.setEnabled(false);
        executeFlowButton.addActionListener(e -> executeFlow());
        controlPanel.add(executeFlowButton);
        
        executeFlowResponsiveButton = new JButton("Execute flow responsive");
        executeFlowResponsiveButton.setEnabled(false);
        executeFlowResponsiveButton.addActionListener(
            e -> executeFlowResponsive());
        controlPanel.add(executeFlowResponsiveButton);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancel());
        controlPanel.add(cancelButton);
        
        return controlPanel;
    }
    
    /**
     * Set the {@link FlowExecutorControl} 
     * 
     * @param flowExecutorControl The {@link FlowExecutorControl}
     */
    void setFlowExecutorControl(FlowExecutorControl flowExecutorControl)
    {
        this.flowExecutorControl = flowExecutorControl;
        if (flowExecutorControl == null)
        {
            executeFlowButton.setEnabled(false);
            executeFlowResponsiveButton.setEnabled(false);
        }
        else
        {
            executeFlowButton.setEnabled(true);
            executeFlowResponsiveButton.setEnabled(true);
        }
    }

    /**
     * Calls {@link FlowExecutorControl#executeFlow()}, in a background
     * thread
     */
    private void executeFlow()
    {
        executeFlowButton.setEnabled(false);
        executeFlowResponsiveButton.setEnabled(false);
        cancelButton.setEnabled(true);
        class FlowExecutorWorker extends SwingWorker<Void, Void>
        {
            @Override
            public Void doInBackground()
            {
                flowExecutorControl.executeFlow();
                return null;
            }

            @Override
            protected void done()
            {
                executeFlowButton.setEnabled(true);
                executeFlowResponsiveButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        }
        FlowExecutorWorker flowExecutorWorker = new FlowExecutorWorker();
        flowExecutorWorker.execute();
    }
    
    /**
     * Calls {@link FlowExecutorControl#executeFlowResponsive()}, in a 
     * background thread
     */
    private void executeFlowResponsive()
    {
        executeFlowButton.setEnabled(false);
        executeFlowResponsiveButton.setEnabled(false);
        cancelButton.setEnabled(true);
        class FlowExecutorWorker extends SwingWorker<Void, Void>
        {
            @Override
            public Void doInBackground()
            {
                flowExecutorControl.executeFlowResponsive();
                return null;
            }

            @Override
            protected void done()
            {
                executeFlowButton.setEnabled(true);
                executeFlowResponsiveButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        }
        FlowExecutorWorker flowExecutorWorker = new FlowExecutorWorker();
        flowExecutorWorker.execute();
    }
    
    
    /**
     * Calls {@link FlowExecutorControl#cancelExecution()}, in a background
     * thread
     */
    private void cancel()
    {
        cancelButton.setEnabled(false);
        class FlowExecutorWorker extends SwingWorker<Void, Void>
        {
            @Override
            public Void doInBackground()
            {
                flowExecutorControl.cancelExecution();
                return null;
            }

            @Override
            protected void done()
            {
                executeFlowButton.setEnabled(true);
                executeFlowResponsiveButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        }
        FlowExecutorWorker flowExecutorWorker = new FlowExecutorWorker();
        flowExecutorWorker.execute();
    }
    
}
