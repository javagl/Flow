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

import de.javagl.flow.Flow;
import de.javagl.flow.MutableFlow;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Interface for one module that may appear in a {@link Flow}.<br>
 * <br> 
 * The general structure of a Module is as follows:
 * <ul>
 *   <li>
 *     It contains a list of {@link #getInputSlots() input slots} which 
 *     provide the input for the computation 
 *   </li> 
 *   <li>
 *     It offers an {@link Module#execute() execute} method that 
 *     performs the actual work 
 *   </li> 
 *   <li>
 *     It contains a list of {@link #getOutputSlots() output slots} which 
 *     receive the result of the computation 
 *   </li> 
 *   <li>
 *     It may optionally have a {@link #getConfiguration() configuration}
 *     that affects how the actual computation is performed. 
 *   </li>
 * </ul>
 * Connections (and thus, a communication) between modules may be
 * established by creating a {@link Link} between the one {@link OutputSlot} 
 * of one module and one {@link InputSlot} of another module.<br>
 * <br> 
 * The properties of a module are summarized via a {@link ModuleInfo}.
 * This {@link ModuleInfo} is passed to the module at construction time.
 * Instances of modules may be created with the {@link Modules} class,
 * although in the greater context of applications using the flow library,
 * custom modules will be created by dedicated factory classes.<br>
 * <br>
 * Implementors of this interface will most likely inherit from the 
 * {@link AbstractModule} or the {@link SimpleAbstractModule} class.<br>
 * <br>
 * The {@link ModuleUtils} class contains utility methods that simplify
 * querying topological information about {@link Module} instances.
 */
public interface Module
{
    /**
     * Returns the {@link ModuleInfo} that describes the 
     * unmodifiable, basic properties of this module.
     * 
     * @return The {@link ModuleInfo}
     */
    ModuleInfo getModuleInfo();
    
    /**
     * Returns an unmodifiable list containing the {@link InputSlot} instances
     * 
     * @return The list of {@link InputSlot} instances
     */
    List<InputSlot> getInputSlots();
    
    /**
     * Returns an unmodifiable list containing the {@link OutputSlot} instances
     * 
     * @return The list of {@link OutputSlot} instances
     */
    List<OutputSlot> getOutputSlots();

    /**
     * Returns the {@link Flow} to which this module belongs, or 
     * <code>null</code> if this module is not part of a {@link Flow}
     *  
     * @return The {@link Flow} to which this module belongs
     */
    Flow getFlow();
    
    /**
     * Set the {@link Flow} to which this module belongs. This method
     * is not intended to be called by clients. It will be called 
     * automatically when the module is added to or removed from
     * a {@link MutableFlow}.
     * 
     * @param flow The {@link Flow} to which this module belongs.
     * May be <code>null</code> when the module should not belong
     * to any {@link Flow}
     */
    void setFlow(Flow flow);
    
    /**
     * Executes this module. This will usually consist of <br>
     * <ul>
     *   <li>
     *     notification of the {@link ModuleExecutionListener} instances 
     *     about the start of the execution
     *   </li>
     *   <li>
     *     obtaining the input values from the {@link InputSlot} instances
     *   </li>
     *   <li>
     *     notification of the {@link ModuleExecutionListener} instances 
     *     about the start of the computation
     *   </li>
     *   <li>performing the actual computation</li>
     *   <li>
     *     notification of the {@link ModuleExecutionListener} instances 
     *     about the end of the computation
     *   </li>
     *   <li>
     *     forwarding the outputs to the {@link OutputSlot} instances
     *   </li>
     *   <li>
     *     notification of the {@link ModuleExecutionListener} instances 
     *     about the end of the execution
     *   </li>
     * </ul>
     * The {@link AbstractModule} and {@link SimpleAbstractModule} classes
     * offer stub implementations of this method.
     */
    void execute();
    
    /**
     * Add the given {@link ModuleExecutionListener} to be informed
     * about the execution status of this module.
     * 
     * @param moduleExecutionListener The listener to add
     */
    void addModuleExecutionListener(
        ModuleExecutionListener moduleExecutionListener);

    /**
     * Remove the given {@link ModuleExecutionListener} 
     * 
     * @param moduleExecutionListener The listener to remove
     */
    void removeModuleExecutionListener(
        ModuleExecutionListener moduleExecutionListener);
    

    /**
     * Returns the configuration of this module.<br>
     * <br>
     * The general contract for the configuration of a module is as follows:
     * <br>
     * <br>
     * <ul>
     *   <li>
     *     The instance that is returned by this method may not change. 
     *     That is, when this method is called more than once during the 
     *     execution of the application, it must consistently return the 
     *     identical object - although, of course, the values of the 
     *     fields of this instance may be modified. 
     *   </li>
     *   <li>
     *     The values of the fields of the configuration object should 
     *     usually not change during the execution of the module. During 
     *     the execution of the module, the fields of the configuration 
     *     may be <i>read</i>, but should not be <i>written</i>. <br >
     *     An exception to this rule may be modules that store "default" 
     *     input values for certain input slots in the configuration.
     *     In this special case, it is feasible to use the values that are
     *     obtained from the input slots to update the configuration 
     *     accordingly, right before the actual processing is performed.  
     *   </li>
     *   <li>
     *     The configuration is usually a Java Bean, although it not
     *     necessarily has to be one. In certain contexts (for example,
     *     in a GUI-based application that is used for editing a flow),
     *     an introspection of the configuration object may be performed,
     *     and PropertyChangeListeners may be added if the object offers
     *     methods to add and remove PropertyChangeListeners.
     *   </li>
     *   <li>
     *     If this module does not have configuration settings, 
     *     this method will return <code>null</code>
     *   </li>
     * </ul>
     * @return The configuration of this module
     */
    Object getConfiguration();
    
    /**
     * Set the given configuration for this module. See the general 
     * contract for the {@link #getConfiguration() configuration} for
     * details. Note that consequently, the module will <b>not</b>
     * store a reference to the given object. Instead, all properties
     * of the configuration of this module will be set to have the
     * same value as the corresponding property of the given object.
     * Type safety can not be guaranteed here, and thus, the caller  
     * must take care to pass only objects that are suitable for the 
     * module that this method is called on.
     *  
     * @param configuration The configuration
     * @throws UnsupportedOperationException if this method is called
     * with a non-<code>null</code> parameter on a module that does
     * not store a configuration
     */
    void setConfiguration(Object configuration);
}
