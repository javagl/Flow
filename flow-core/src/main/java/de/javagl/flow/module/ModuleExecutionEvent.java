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

import java.util.EventObject;

/**
 * An event that is passed to a {@link ModuleExecutionListener}
 * to indicate a state change in the execution of a {@link Module} 
 */
public final class ModuleExecutionEvent extends EventObject
{
    /**
     * The {@link Module} from which this event originated
     */
    private final Module module;
    
    /**
     * The optional message that is associated with this event
     */
    private final String message;
    
    /**
     * The optional progress of the module execution, as a value
     * in [0,1]. A negative value indicates an unknown progress.
     */
    private final double progress;
    
    /**
     * The optional error that happened during the execution
     */
    private final Throwable throwable;
    
    /**
     * Creates a new event for the given {@link Module}
     * 
     * @param module The {@link Module} from which this event originated
     */
    ModuleExecutionEvent(Module module)
    {
        this(module, null, -1.0, null);
    }

    /**
     * Creates a new event with the given parameters.
     * 
     * @param module The {@link Module} from which this event originated
     * @param message The optional message that is associated with this event
     * @param progress The optional progress of the module execution, as a 
     * value in [0,1]. A negative value indicates an unknown progress.
     */
    ModuleExecutionEvent(Module module, String message, double progress)
    {
        this(module, message, -1.0, null);
    }

    /**
     * Creates a new event with the given parameters.
     * 
     * @param module The {@link Module} from which this event originated
     * @param throwable The optional error that happened during execution
     */
    ModuleExecutionEvent(Module module, Throwable throwable)
    {
        this(module, null, -1.0, throwable);
    }
    
    /**
     * Creates a new event with the given parameters.
     * 
     * @param module The {@link Module} from which this event originated
     * @param message The optional message that is associated with this event
     * @param progress The optional progress of the module execution, as a 
     * value in [0,1]. A negative value indicates an unknown progress.
     * @param throwable The optional error that happened during execution
     */
    private ModuleExecutionEvent(
        Module module, String message, double progress, Throwable throwable)
    {
        super(module);
        this.module = module;
        this.message = message;
        this.progress = progress;
        this.throwable = throwable;
    }

    /**
     * Returns the {@link Module} from which this event originated
     * 
     * @return The {@link Module} from which this event originated
     */
    public Module getModule()
    {
        return module;
    }

    /**
     * Returns the optional message that is associated with this event
     * 
     * @return The optional message that is associated with this event
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Returns the optional progress of the module execution, as a 
     * value in [0,1]. A negative value indicates an unknown progress.
     * 
     * @return The progress of the module execution
     */
    public double getProgress()
    {
        return progress;
    }
    
    /**
     * Returns the optional error that happened during the execution
     * 
     * @return The optional error that happened during the execution
     */
    public Throwable getThrowable()
    {
        return throwable;
    }
    
}
