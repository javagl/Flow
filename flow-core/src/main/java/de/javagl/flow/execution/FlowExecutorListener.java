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

/**
 * Interface for classes that want to be informed about the execution
 * progress of a {@link FlowExecutor}.
 * 
 * TODO: The FlowExecutorListener interface might have to become more 
 * fine-grained. For example, there might be dedicated methods in
 * addition to "afterExecution", indicating whether the execution
 * was cancelled or terminated with an error. Additionally, there
 * may be intermediate events, indicating that one module or a
 * set of modules has been executed. This might cause some overlap
 * with the ModuleExecutionListener interface. This might also
 * affect the semantics of the "cancelled" flag in the 
 * {@link FlowExecutorEvent} class.
 */
public interface FlowExecutorListener
{
    /**
     * Will be called before the actual execution of a {@link FlowExecutor}
     *  
     * @param flowExecutorEvent The {@link FlowExecutorEvent}
     */
    void beforeExecution(FlowExecutorEvent flowExecutorEvent);
    
    /**
     * Will be called when a {@link FlowExecutor} finished executing 
     * 
     * @param flowExecutorEvent The {@link FlowExecutorEvent}
     */
    void afterExecution(FlowExecutorEvent flowExecutorEvent);
}
