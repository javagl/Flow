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

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Interface for classes that want to be informed about the execution
 * progress of a {@link Module}.
 */
public interface ModuleExecutionListener
{
    /**
     * Will be called before the actual execution of a {@link Module}
     * (even before the input data is obtained).
     *  
     * @param moduleExecutionEvent The {@link ModuleExecutionEvent}
     */
    void beforeExecution(ModuleExecutionEvent moduleExecutionEvent);
    
    /**
     * Will be called after a {@link Module} has obtained all its
     * inputs from its {@link InputSlot} instances, and is about to 
     * perform the actual computation.
     * 
     * @param moduleExecutionEvent The {@link ModuleExecutionEvent}
     */
    void beforeProcessing(ModuleExecutionEvent moduleExecutionEvent);
    
    /**
     * May be called when the progress of the execution of a {@link Module} 
     * has changed. 
     * 
     * @param moduleExecutionEvent The {@link ModuleExecutionEvent}
     */
    void progressChanged(ModuleExecutionEvent moduleExecutionEvent);
    
    /**
     * Will be called directly after a {@link Module} has performed
     * its actual computation, but before its results are passed to the
     * {@link OutputSlot} instances.
     * 
     * @param moduleExecutionEvent The {@link ModuleExecutionEvent}
     */
    void afterProcessing(ModuleExecutionEvent moduleExecutionEvent);
    
    /**
     * Will be called when a {@link Module} has passed its 
     * computation results to its {@link OutputSlot} instances. 
     * 
     * @param moduleExecutionEvent The {@link ModuleExecutionEvent}
     */
    void afterExecution(ModuleExecutionEvent moduleExecutionEvent);
}
