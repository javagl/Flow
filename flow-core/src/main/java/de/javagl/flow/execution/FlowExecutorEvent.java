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

import java.util.EventObject;

import de.javagl.flow.Flow;

/**
 * An event that is passed to a {@link FlowExecutorListener}
 * to inform it about the progress of a {@link FlowExecutor} 
 */
public final class FlowExecutorEvent extends EventObject
{
    /**
     * The {@link FlowExecutor} from which this event originated
     */
    private final FlowExecutor flowExecutor;
    
    /**
     * The {@link Flow} that is executed
     */
    private final Flow flow;
    
    /**
     * Creates a new event for the given {@link FlowExecutor}
     * 
     * @param flowExecutor The {@link FlowExecutor} from which this event 
     * originated
     */
    FlowExecutorEvent(FlowExecutor flowExecutor)
    {
        this(flowExecutor, null);
    }

    /**
     * Creates a new event for the given {@link FlowExecutor}
     * 
     * @param flowExecutor The {@link FlowExecutor} from which this event 
     * originated
     * @param flow The {@link Flow} that is executed
     */
    FlowExecutorEvent(FlowExecutor flowExecutor, Flow flow)
    {
        super(flowExecutor);
        this.flowExecutor = flowExecutor;
        this.flow = flow;
    }
    
    /**
     * Returns the {@link FlowExecutor} from which this event originated
     * 
     * @return The {@link FlowExecutor}
     */
    public FlowExecutor getFlowExecutor()
    {
        return flowExecutor;
    }
    
    /**
     * Returns the {@link Flow} that is executed
     * 
     * @return The {@link Flow}
     */
    public Flow getFlow()
    {
        return flow;
    }
    
}
