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

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Implementation of the {@link FlowExecutorListener} interface that
 * simply prints all operations as logging messages.
 */
public final class LoggingFlowExecutorListener 
    implements FlowExecutorListener
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(LoggingFlowExecutorListener.class.getName());
    
    /**
     * The log level that will be used for the output
     */
    private Level level;

    /**
     * Creates a new instance with an unspecified default log level
     */
    public LoggingFlowExecutorListener()
    {
        this(Level.INFO);
    }
    
    /**
     * Creates a new instance with the given log level
     * 
     * @param level The level
     */
    public LoggingFlowExecutorListener(Level level)
    {
        this.level = Objects.requireNonNull(level, "The level may not be null");
    }
    
    @Override
    public void beforeExecution(FlowExecutorEvent flowExecutorEvent)
    {
        logger.log(level, "beforeExecution  "
            + flowExecutorEvent.getFlow());
    }

    @Override
    public void afterExecution(FlowExecutorEvent flowExecutorEvent)
    {
        logger.log(level, "afterExecution   "
            + flowExecutorEvent.getFlow() + ", " 
            + "errors: " + flowExecutorEvent.getErrors());
    }

    
}