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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewType;

/**
 * Abstract base class for a {@link ModuleCreator}. <br>
 * <br>
 * Subclasses only have to call the constructor of this class which expects 
 * a non-<code>null</code> {@link ModuleInfo} object that describes the 
 * {@link Module} objects that will be created, and implement the 
 * {@link #createModule()} method to actually create the {@link Module} 
 * instances.<br>
 * <br>
 * More complex module types may involve {@link ModuleView} objects. 
 * When a graphical representation of a {@link Module} is about to
 * be created, then the {@link ModuleCreator} will be queried for
 * the {@link #getSupportedModuleViewTypes() supported ModuleView types},
 * and the {@link #createModuleView(ModuleViewType)} method will be
 * called with these types.<br>
 * <br>
 * This base implementation does not support any {@link ModuleViewType}.
 * Subclasses may override the {@link #getSupportedModuleViewTypes()},
 * or pass the supported module view types to the constructor.
 * 
 */
public abstract class AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo} describing the the {@link Module} instances 
     * created by this class
     */
    private final ModuleInfo moduleInfo;
    
    /**
     * The {@link #getInstantiationString() instantiation string}
     */
    private final String instantiationString;
    
    /**
     * The list of {@link ModuleViewType} objects describing the 
     * {@link ModuleView} types that may be created by this class.
     */
    private final List<ModuleViewType> supportedModuleViewTypes;
    
    /**
     * Creates a new instance that can create {@link Module} instances 
     * according to the given {@link ModuleInfo}
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    protected AbstractModuleCreator(ModuleInfo moduleInfo)
    {
        this(moduleInfo, Collections.emptyList());
    }
    
    /**
     * Creates a new instance that can create {@link Module} instances 
     * according to the given {@link ModuleInfo}
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param moduleViewTypes The supported {@link ModuleViewType} objects
     */
    protected AbstractModuleCreator(ModuleInfo moduleInfo, 
        Collection<? extends ModuleViewType> moduleViewTypes)
    {
        this.moduleInfo = Objects.requireNonNull(
            moduleInfo, "The moduleInfo may not be null");
        this.instantiationString = 
            ModuleCreatorInstantiator.createInstantiationString(getClass());
        this.supportedModuleViewTypes = 
            new ArrayList<ModuleViewType>(moduleViewTypes);
    }
    

    /**
     * Creates a new instance that can create {@link Module} instances 
     * according to the given {@link ModuleInfo}
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param instantiationString The {@link #getInstantiationString() 
     * instantiation string} 
     */
    protected AbstractModuleCreator(
        ModuleInfo moduleInfo, 
        String instantiationString)
    {
        this.moduleInfo = Objects.requireNonNull(
            moduleInfo, "The moduleInfo may not be null");
        this.instantiationString = instantiationString; 
        this.supportedModuleViewTypes = new ArrayList<ModuleViewType>();
    }

    /**
     * Set the {@link ModuleViewType} objects that will be reported as 
     * the {@link #getSupportedModuleViewTypes() supported ModuleView types},
     * and will be passed to {@link #createModuleView(ModuleViewType)}.
     * 
     * @param moduleViewTypes The supported {@link ModuleViewType} objects
     */
    protected final void setSupportedModuleViewTypes(
        ModuleViewType ... moduleViewTypes)
    {
        setSupportedModuleViewTypes(Arrays.asList(moduleViewTypes));
    }
    
    /**
     * Set the {@link ModuleViewType} objects that will be reported as 
     * the {@link #getSupportedModuleViewTypes() supported ModuleView types},
     * and will be passed to {@link #createModuleView(ModuleViewType)}.
     * 
     * @param moduleViewTypes The supported {@link ModuleViewType} objects
     */
    protected final void setSupportedModuleViewTypes(
        Collection<? extends ModuleViewType> moduleViewTypes)
    {
        this.supportedModuleViewTypes.clear();
        this.supportedModuleViewTypes.addAll(moduleViewTypes);
    }

    @Override
    public final ModuleInfo getModuleInfo()
    {
        return moduleInfo;
    }
    
    @Override
    public final String getInstantiationString()
    {
        return instantiationString;
    }
    
    @Override
    public final List<ModuleViewType> getSupportedModuleViewTypes()
    {
        return Collections.unmodifiableList(supportedModuleViewTypes);
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return moduleInfo.toString();
    }
    
}
