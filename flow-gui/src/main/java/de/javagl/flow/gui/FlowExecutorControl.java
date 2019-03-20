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

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.flow.Flow;
import de.javagl.flow.execution.FlowExecutor;
import de.javagl.flow.execution.FlowExecutors;
import de.javagl.flow.execution.LoggingFlowExecutorListener;
import de.javagl.flow.workspace.FlowWorkspace;

/**
 * A (preliminary, internal) class that manages the execution of 
 * {@link Flow} instances in the UI.
 */
final class FlowExecutorControl
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(FlowExecutorControl.class.getName());
    
    /**
     * The {@link FlowWorkspace} on which this control operates
     */
    private FlowWorkspace flowWorkspace;
    
    /**
     * The {@link FlowExecutor}
     */
    private FlowExecutor flowExecutor;
    
    /**
     * Set the {@link FlowWorkspace} on which this control operates
     * 
     * @param flowWorkspace The {@link FlowWorkspace}
     */
    void setFlowWorkspace(FlowWorkspace flowWorkspace)
    {
        this.flowWorkspace = flowWorkspace;
    }
    
    /**
     * Execute the flow from the current {@link FlowWorkspace},
     * blocking the calling thread until the execution completed
     */
    void executeFlow()
    {
        flowExecutor = FlowExecutors.createDefault();
        flowExecutor.addFlowExecutorListener(new LoggingFlowExecutorListener());
        flowExecutor.execute(flowWorkspace.getFlow());
        flowExecutor = null;
    }
    
    /**
     * Execute the flow from the current {@link FlowWorkspace},
     * blocking the calling thread until the execution completed
     */
    void executeFlowResponsive()
    {
        flowExecutor = FlowExecutors.createResponsive();
        flowExecutor.addFlowExecutorListener(new LoggingFlowExecutorListener());
        flowExecutor.execute(flowWorkspace.getFlow());
        flowExecutor = null;
    }
    
    /**
     * Cancel any execution that may currently be in progress
     */
    void cancelExecution()
    {
        if (flowExecutor != null)
        {
            Exception error = flowExecutor.finishExecution(
                100, TimeUnit.MILLISECONDS);
            if (error != null)
            {
                logger.log(Level.WARNING, "Warning: " + error, error);
            }
            flowExecutor = null;
        }
    }
}
