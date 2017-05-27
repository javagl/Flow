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
package de.javagl.flow;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a {@link FlowListener} that simply
 * prints the operations in form of logging messages. 
 */
public final class LoggingFlowListener implements FlowListener
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(LoggingFlowListener.class.getName());
    
    /**
     * The log level that will be used for the output
     */
    private Level level;
    
    /**
     * Create a logging flow listener with an INFO log level
     */
    public LoggingFlowListener()
    {
        this.level = Level.INFO;
    }

    /**
     * Set the log level that should be used for the output
     * 
     * @param level The log level
     */
    public void setLevel(Level level)
    {
        this.level = level;
    }
    
    @Override
    public void moduleAdded(FlowEvent flowEvent)
    {
        logger.log(level, "moduleAdded   "+flowEvent.getModule());
    }

    @Override
    public void moduleRemoved(FlowEvent flowEvent)
    {
        logger.log(level, "moduleRemoved "+flowEvent.getModule());
    }

    @Override
    public void linkAdded(FlowEvent flowEvent)
    {
        logger.log(level, "linkAdded     "+flowEvent.getLink());
    }

    @Override
    public void linkRemoved(FlowEvent flowEvent)
    {
        logger.log(level, "linkRemoved   "+flowEvent.getLink());
    }
    
}