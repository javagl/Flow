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

package de.javagl.flow.module.creation;

import java.util.List;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewType;


/**
 * Interface for classes that can create {@link Module} instances and the
 * corresponding {@link ModuleView} instances. <br>
 * <br>
 * Implementations of this interface are the basis for maintaining the
 * information about the available {@link Module} types in an application.
 * The {@link ModuleCreator} implementations are used as a reference, for
 * example, for creating {@link Module} instances inside the application
 * (e.g. via drag and drop). <br>
 * <br>
 * More importantly, the {@link ModuleCreator} implementations are also 
 * used for <i>persistence</i>: It may not be possible to persist an
 * actual {@link Module} or a {@link ModuleView}. So the {@link ModuleCreator}
 * provides an {@link #getInstantiationString() instantiation string}, which
 * is an unspecified string that may be used to create an instance of 
 * the {@link ModuleCreator} by passing this string to the  
 * {@link ModuleCreatorInstantiator#createFromInstantiationString(String)}
 * method.<br>
 * <br>
 * Implementors of this interface will usually extend the 
 * {@link AbstractModuleCreator} class. This class already creates a proper
 * instantiation string that can be used to create the {@link ModuleCreator}
 * instance at runtime. Such an implementation therefore has to be 
 * a public class with a public default (no-argument) constructor.
 */
public interface ModuleCreator
{
    /**
     * Returns the {@link ModuleInfo} that describes the {@link Module} 
     * instances that are created by this creator.
     * 
     * @return The {@link ModuleInfo}
     */
    ModuleInfo getModuleInfo();
    
    /**
     * Create a {@link Module}
     * 
     * @return The new {@link Module}
     */
    Module createModule();
    
    /**
     * Returns the instantiation string for this creator. The instantiation
     * string is a string that can be passed to the
     * {@link ModuleCreatorInstantiator#createFromInstantiationString(String)}
     * method in order to create an instance of this ModuleCreator.
     * This is used for persistence, and should not be used by clients.
     * 
     * @return The instantiation string
     */
    String getInstantiationString();
    
    /**
     * Returns an unmodifiable list containing the {@link ModuleViewType}
     * values that are valid for the {@link Module} created by this creator. 
     * The elements in this list may be passed to the 
     * {@link #createModuleView(ModuleViewType)} method to create
     * the corresponding {@link ModuleView}, which the {@link Module}
     * created by this creator may be assigned to.
     * 
     * @return The {@link ModuleViewType} values supported by the {@link Module}
     */
    List<ModuleViewType> getSupportedModuleViewTypes();

    /**
     * Create the {@link ModuleView} for the given {@link ModuleViewType}.
     * The given {@link ModuleViewType} must be one of the types contained
     * in the list that can be obtained with 
     * {@link #getSupportedModuleViewTypes()}. This method will then create
     * {@link ModuleView} to which the {@link Module} that was created
     * with {@link #createModule()} may be assigned.<br>
     * <br>
     * If the given {@link ModuleViewType} is not supported, then 
     * <code>null</code> will be returned.
     * 
     * @param moduleViewType The {@link ModuleViewType} 
     * @return The {@link ModuleView} for the given {@link ModuleViewType},
     * or <code>null</code> if the given {@link ModuleViewType} is not
     * supported.
     */
    ModuleView createModuleView(ModuleViewType moduleViewType);
    
}
