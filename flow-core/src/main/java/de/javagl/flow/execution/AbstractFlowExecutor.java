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
package de.javagl.flow.execution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javagl.flow.Flow;

/**
 * Abstract base implementation of a {@link FlowExecutor}. This implementation
 * only manages the {@link FlowExecutorListener} instances.
 */
abstract class AbstractFlowExecutor implements FlowExecutor
{
    /**
     * The registered {@link FlowExecutorListener} instances
     */
    private final List<FlowExecutorListener> flowExecutorListeners;
    
    /**
     * Default constructor
     */
    protected AbstractFlowExecutor()
    {
        flowExecutorListeners = 
            new CopyOnWriteArrayList<FlowExecutorListener>();
    }
    
    /**
     * Inform all registered {@link FlowExecutorListener} instances that
     * the given {@link Flow} is about to be executed
     * 
     * @param flow The {@link Flow}
     */
    protected final void fireBeforeExecution(Flow flow)
    {
        if (!flowExecutorListeners.isEmpty())
        {
            FlowExecutorEvent flowExecutorEvent = 
                new FlowExecutorEvent(this, flow);
            for (FlowExecutorListener listener : flowExecutorListeners)
            {
                listener.beforeExecution(flowExecutorEvent);
            }
        }
    }
    
    /**
     * Inform all registered {@link FlowExecutorListener} instances that
     * the given {@link Flow} was executed
     * 
     * @param flow The {@link Flow}
     */
    protected final void fireAfterExecution(Flow flow)
    {
        if (!flowExecutorListeners.isEmpty())
        {
            FlowExecutorEvent flowExecutorEvent = 
                new FlowExecutorEvent(this, flow);
            for (FlowExecutorListener listener : flowExecutorListeners)
            {
                listener.afterExecution(flowExecutorEvent);
            }
        }
    }
    
    @Override
    public final void addFlowExecutorListener(
        FlowExecutorListener flowExecutorListener)
    {
        flowExecutorListeners.add(flowExecutorListener);
    }

    @Override
    public final void removeFlowExecutorListener(
        FlowExecutorListener flowExecutorListener)
    {
        flowExecutorListeners.remove(flowExecutorListener);
    }

}
