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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import de.javagl.flow.Flow;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Abstract base implementation of a {@link Module}. It manages the 
 * {@link ModuleExecutionListener} instances and offers some convenience 
 * methods for {@link #obtainInput(int) obtaining} data from an 
 * {@link InputSlot}, for {@link #forwardOutput(int, Object) forwarding} 
 * data to an {@link OutputSlot}, and it contains a stub implementation 
 * of the {@link #execute()} method. <br>
 * <br>
 * Implementors of this class will usually override the {@link #process()}
 * or the {@link #processCore(Object[], Object[])} method, in order to
 * perform the implementation-specific tasks.
 */
public abstract class AbstractModule implements Module
{
    /**
     * A counter for generating IDs. This is only used for generating the
     * {@link #idString}.
     */
    private static AtomicInteger idCounter = new AtomicInteger();
    
    /**
     * The {@link ModuleInfo} describing this module
     */
    private final ModuleInfo moduleInfo;

    /**
     * The {@link Flow} to which this module belongs
     */
    private Flow flow;
    
    /**
     * The {@link ModuleExecutionListener} instances that want to be 
     * informed about the execution process of this module.
     */
    private final List<ModuleExecutionListener> moduleExecutionListeners;

    /**
     * A string that will be appended to the default string representation
     * of a module, as a disambiguation. This does not carry any meaning.
     */
    private final String idString;
    
    /**
     * Creates new module with the given {@link ModuleInfo}
     * 
     * @param moduleInfo The {@link ModuleInfo}. May not be <code>null</code>.
     */
    protected AbstractModule(ModuleInfo moduleInfo)
    {
        this.moduleInfo = Objects.requireNonNull(
            moduleInfo, "The moduleInfo may not be null");
        this.moduleExecutionListeners = 
            new CopyOnWriteArrayList<ModuleExecutionListener>();
        
        idString = String.valueOf(idCounter.getAndIncrement());
    }

    @Override
    public final ModuleInfo getModuleInfo()
    {
        return moduleInfo;
    }

    @Override
    public final void setFlow(Flow flow)
    {
        this.flow = flow;
    }
    
    @Override
    public final Flow getFlow()
    {
        return flow;
    }
    
    @Override
    public Object getConfiguration()
    {
        return null;
    }
    
    @Override
    public void setConfiguration(Object configuration)
    {
        // Default implementation: AbstractModule does not accept any 
        // configuration. Subclasses can overwrite the getConfiguration
        // and setConfiguration methods according to their needs.
        if (configuration != null)
        {
            throw new UnsupportedOperationException(
                "Module " + getClass().getName() 
                + " does not have a configuration");
        }
    }

    /**
     * Convenience method to obtain the input from the {@link Link} that
     * is set as the input for the {@link InputSlot} with the given index.
     * 
     * @param index The index of the input slot.
     * @return The value provided by the {@link Link} that is set as the
     * input for the {@link InputSlot} with the given index, or 
     * <code>null</code> if there is no input set for the specified slot.
     */
    protected final Object obtainInput(int index)
    {
        Link inputLink = getInputSlots().get(index).getInputLink();
        if (inputLink == null)
        {
            return null;
        }
        return inputLink.provide();
    }
    
    /**
     * Forward the given value to each {@link Link} that is set as output
     * for the {@link OutputSlot} with the given index.
     * 
     * @param index The index of the output slot
     * @param value The value to forward to each output {@link Link}
     */
    protected final void forwardOutput(int index, Object value)
    {
        OutputSlot outputSlot = getOutputSlots().get(index);
        for (Link output : outputSlot.getOutputLinks())
        {
            output.accept(value);
        }
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * This is a stub implementation of the {@link Module#execute()} method. 
     * This method will delegate the actual processing to the 
     * {@link #process()} method, and handle the notification of the 
     * {@link ModuleExecutionListener} instances about the start and end of 
     * the execution. <br>
     * <br>
     */
    @Override
    public final void execute()
    {
        fireBeforeExecution();
        Throwable throwable = null;
        try
        {
            process();
        }
        catch (Exception t)
        {
            throwable = t;
        }
        finally
        {
            fireAfterExecution(throwable);
        }
        
        // TODO Should this really be rethrown here?
        if (throwable != null)
        {
            throw new RuntimeException(throwable);
        }
    }
    
    /**
     * This method performs the implementation-specific task of this 
     * {@link Module}. Implementors may either override this method,
     * or the {@link #processCore(Object[], Object[])} method in order
     * to perform their implementation specific tasks.<br>
     * <br>
     * This method will be called in the {@link #execute()} method, after 
     * the {@link ModuleExecutionListener} instances have been informed about 
     * the start of the execution, and before the listeners are informed 
     * about the end of the execution. Any exception thrown by this 
     * method will be passed to the {@link #fireAfterExecution(Throwable)} 
     * method, and re-thrown by the {@link #execute()} method.<br>
     * <br>
     * Implementors of this method should inform listeners about the state of 
     * the computation. Particularly, they should at least call the 
     * {@link #fireBeforeProcessing()} method after they have obtained
     * the input for the computation, and the {@link #fireAfterProcessing()} 
     * method before they forward the output of the computation to the 
     * {@link OutputSlot}.<br>  
     * <br>
     * If desired, they may call {@link #fireProgressChanged(String, double)}
     * in order to provide more detailed information about the progress.<br>
     * <br>
     * Thus, a typical implementation of this method may look like this:
     * <pre><code>
     * Object input0 = obtainInput(0);
     * Object input1 = obtainInput(1);
     * fireBeforeProcessing();
     * 
     * Object output0 = computeSomeResult(input0, input1);
     * // Optionally inform listeners about the progress:
     * fireProgressChanged("Half way done...", 0.5);
     * Object output1 = computeSomeOtherResult(input0, input1);
     * 
     * fireAfterProcessing();
     * forwardOutput(0, output0);
     * forwardOutput(1, output1);
     * </code></pre>
     * <br>
     * The implementor may choose whether and how he handles invalid inputs.
     * For example, when an input value is <code>null</code>, this may be
     * an anticipated situation and should be handled accordingly, for 
     * example by skipping the actual computation and forwarding
     * <code>null</code> as the result. In other cases, <code>null</code>
     * may be an invalid input, and a {@link NullPointerException} may be
     * thrown. <br>
     * <br>
     * The default implementation of this method does the following:
     * <ul>
     *   <li>It obtains the input values</li>
     *   <li>It calls {@link #fireBeforeProcessing()}</li>
     *   <li>
     *     It calls {@link #processCore(Object[], Object[])}. This method will
     *     receive the input values, and may write the output values into
     *     the given output array.
     *   </li>
     *   <li>It calls {@link #fireAfterProcessing()}</li>
     *   <li>It forwards the output values</li>
     * </ul>
     * For simple module implementations, users may thus choose to only
     * implement the {@link #processCore(Object[], Object[])} method 
     * accordingly.
     */
    protected void process()
    {
        int numInputs = getInputSlots().size();
        Object inputs[] = new Object[numInputs];
        for (int i=0; i<numInputs; i++)
        {
            inputs[i] = obtainInput(i);
        }
        int numOutputs = getOutputSlots().size();
        Object outputs[] = new Object[numOutputs];
        fireBeforeProcessing();
        processCore(inputs, outputs);
        fireAfterProcessing();
        for (int i=0; i<numOutputs; i++)
        {
            forwardOutput(i, outputs[i]);
        }
    }
    
    /**
     * Empty default implementation of the method for the actual processing,
     * which is called in {@link #process()}. For simple modules, users
     * may choose to only override this method, and keep the default 
     * implementation of the {@link #process()} method.
     * 
     * @param inputs The input values that have been obtained from the 
     * {@link #getInputSlots() input slots}. This may be an empty array 
     * if there are no input slots.
     * @param outputs A pre-allocated array that will receive the output
     * values. The elements of this array will afterwards be forwarded 
     * to the {@link #getOutputSlots() output slots.} This may be an 
     * empty array if there are no output slots.
     */
    protected void processCore(Object inputs[], Object outputs[])
    {
        // Empty default implementation
    }

    @Override
    public String toString()
    {
        return moduleInfo.toString() + " #" + idString;
    }
    

    @Override
    public final void addModuleExecutionListener(
        ModuleExecutionListener moduleExecutionListener)
    {
        moduleExecutionListeners.add(moduleExecutionListener);
    }

    @Override
    public final void removeModuleExecutionListener(
        ModuleExecutionListener moduleExecutionListener)
    {
        moduleExecutionListeners.remove(moduleExecutionListener);
    }

    /**
     * Notify all registered {@link ModuleExecutionListener} instances that the
     * {@link #execute()} method was entered.
     */
    protected final void fireBeforeExecution()
    {
        if (!moduleExecutionListeners.isEmpty())
        {
            ModuleExecutionEvent moduleExecutionEvent =
                new ModuleExecutionEvent(this);
            for (ModuleExecutionListener listener : moduleExecutionListeners)
            {
                listener.beforeExecution(moduleExecutionEvent);
            }
        }
    }

    /**
     * Notify all registered {@link ModuleExecutionListener} instances that the
     * {@link #execute()} method is about to be left.
     * 
     * @param t The throwable that may have been caused by this module
     */
    protected final void fireAfterExecution(Throwable t)
    {
        if (!moduleExecutionListeners.isEmpty())
        {
            ModuleExecutionEvent moduleExecutionEvent =
                new ModuleExecutionEvent(this, t);
            for (ModuleExecutionListener listener : moduleExecutionListeners)
            {
                listener.afterExecution(moduleExecutionEvent);
            }
        }
    }
    
    /**
     * Notify all registered {@link ModuleExecutionListener} instances that the
     * input data has been obtained from the input slots, and 
     * the actual computation is about to start.
     */
    protected final void fireBeforeProcessing()
    {
        if (!moduleExecutionListeners.isEmpty())
        {
            ModuleExecutionEvent moduleExecutionEvent =
                new ModuleExecutionEvent(this);
            for (ModuleExecutionListener listener : moduleExecutionListeners)
            {
                listener.beforeProcessing(moduleExecutionEvent);
            }
        }
    }
    
    /**
     * Notify all registered {@link ModuleExecutionListener} instances that the
     * the actual computation has finished, and the output is about
     * to be forwarded to the output slots.
     */
    protected final void fireAfterProcessing()
    {
        if (!moduleExecutionListeners.isEmpty())
        {
            ModuleExecutionEvent moduleExecutionEvent =
                new ModuleExecutionEvent(this);
            for (ModuleExecutionListener listener : moduleExecutionListeners)
            {
                listener.afterProcessing(moduleExecutionEvent);
            }
        }
    }
    
    /**
     * Notify each registered {@link ModuleExecutionListener} about the
     * computation progress
     * 
     * @param message The progress message
     * @param progress The progress as a value in [0,1]. A negative value
     * will be considered as an unspecified/unknown progress. 
     */
    protected final void fireProgressChanged(String message, double progress)
    {
        if (!moduleExecutionListeners.isEmpty())
        {
            ModuleExecutionEvent moduleExecutionEvent =
                new ModuleExecutionEvent(this, message, progress);
            for (ModuleExecutionListener listener : moduleExecutionListeners)
            {
                listener.progressChanged(moduleExecutionEvent);
            }
        }
    }

    
}