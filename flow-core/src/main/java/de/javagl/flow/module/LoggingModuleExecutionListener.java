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
package de.javagl.flow.module;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Implementation of the {@link ModuleExecutionListener} interface that
 * simply prints all operations as logging messages.
 */
public final class LoggingModuleExecutionListener 
    implements ModuleExecutionListener
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(LoggingModuleExecutionListener.class.getName());
    
    /**
     * The log level that will be used for the output
     */
    private Level level = Level.INFO;

    /**
     * Creates a new instance with an unspecified default log level
     */
    public LoggingModuleExecutionListener()
    {
        this(Level.INFO);
    }
    
    /**
     * Creates a new instance with the given log level
     * 
     * @param level The level
     */
    public LoggingModuleExecutionListener(Level level)
    {
        this.level = Objects.requireNonNull(level, "The level may not be null");
    }
    
    @Override
    public void beforeExecution(ModuleExecutionEvent moduleExecutionEvent)
    {
        logger.log(level, "beforeExecution  "
            + moduleExecutionEvent.getModule());
    }

    @Override
    public void beforeProcessing(ModuleExecutionEvent moduleExecutionEvent)
    {
        logger.log(level, "beforeProcessing "
            + moduleExecutionEvent.getModule());
    }

    @Override
    public void progressChanged(ModuleExecutionEvent moduleExecutionEvent)
    {
        logger.log(level, "progressChanged  "
            + moduleExecutionEvent.getModule() + ", "
            + moduleExecutionEvent.getMessage()+ ", "
            + moduleExecutionEvent.getProgress());
    }

    @Override
    public void afterProcessing(ModuleExecutionEvent moduleExecutionEvent)
    {
        logger.log(level, "afterProcessing  "
            + moduleExecutionEvent.getModule());
    }

    @Override
    public void afterExecution(ModuleExecutionEvent moduleExecutionEvent)
    {
        logger.log(level, "afterExecution   "
            + moduleExecutionEvent.getModule() + ", " 
            + "Error?: " + moduleExecutionEvent.getThrowable());
    }
    
}