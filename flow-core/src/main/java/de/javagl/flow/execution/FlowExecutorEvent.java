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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

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
     * The list of errors that have been caused by the execution
     */
    private final List<Throwable> errors;
    
    /**
     * Whether the execution was cancelled
     * 
     * TODO The exact semantics of this flag have to be sorted out.
     * Also see notes in {@link FlowExecutorListener}
     */
    private final boolean cancelled;
    
    /**
     * Creates a new event for the given {@link FlowExecutor}
     * 
     * @param flowExecutor The {@link FlowExecutor} from which this event 
     * originated
     * @param flow The {@link Flow} that is executed
     * @param cancelled Whether the execution was cancelled
     * @param errors The errors that have been caused by the execution.
     * An unmodifiable copy of the given collection will be stored 
     * internally (or an empty list, of the given collection is 
     * <code>null</code>)
     */
    FlowExecutorEvent(FlowExecutor flowExecutor, Flow flow, boolean cancelled, 
        Collection<? extends Throwable> errors)
    {
        super(flowExecutor);
        this.flowExecutor = flowExecutor;
        this.flow = flow;
        this.cancelled = cancelled;
        if (errors == null)
        {
            this.errors = Collections.emptyList();
        }
        else
        {
            this.errors = Collections.unmodifiableList(
                new ArrayList<Throwable>(errors));
        }
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
    
    /**
     * Returns an unmodifiable list of errors that happened during the
     * execution. This will never be <code>null</code>, but may be 
     * an empty list, if this event indicates the start of the execution,
     * or no errors have been caused during the execution.
     * 
     * @return The errors
     */
    public List<Throwable> getErrors()
    {
        return errors;
    }
    
    /**
     * Returns whether the execution was cancelled
     * 
     * @return Whether the execution was cancelled
     */
    public boolean isCancelled()
    {
        return cancelled;
    }
    
}
