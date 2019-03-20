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

import java.util.concurrent.TimeUnit;

import de.javagl.flow.Flow;
import de.javagl.flow.module.Module;

/**
 * Interface for classes that may execute the {@link Module} instances
 * that are contained in a {@link Flow}.  
 */
public interface FlowExecutor
{
    /**
     * Execute the {@link Module} instances in the given {@link Flow}
     * 
     * @param flow The {@link Flow}
     */
    void execute(Flow flow);
    
    /**
     * Finish the current execution, waiting for up to the specified time
     * if necessary. If the execution does not finish within the specified
     * time, then a best-effort attempt is made to cancel the execution,
     * and the method again waits for the specified time. If the execution
     * still does not finish, then an error is returned indicating this 
     * failure.
     * 
     * @param timeout The timeout
     * @param unit The time unit
     * @return The exception that was caused during the execution, or
     * an <code>InterruptedException</code> exception indicating that the execution could not be finished,
     * or <code>null</code> if the execution finished normally
     */
    Exception finishExecution(long timeout, TimeUnit unit);
    
    /**
     * Add the given {@link FlowExecutorListener} to be informed about
     * the progress of this instance
     * 
     * @param flowExecutorListener The {@link FlowExecutorListener}
     */
    void addFlowExecutorListener(FlowExecutorListener flowExecutorListener);
    
    /**
     * Remove the given {@link FlowExecutorListener}
     * 
     * @param flowExecutorListener The {@link FlowExecutorListener}
     */
    void removeFlowExecutorListener(FlowExecutorListener flowExecutorListener);
    
}
