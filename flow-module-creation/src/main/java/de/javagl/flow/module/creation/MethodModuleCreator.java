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

import java.lang.reflect.Method;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.Modules;

/**
 * A {@link ModuleCreator} that can create {@link Module} instances that
 * call a method.
 */
class MethodModuleCreator 
    extends AbstractModuleCreator 
    implements ModuleCreator
{
    /**
     * The method that should be called
     */
    private final Method method;
    
    /**
     * Create a new instance for the given method
     * 
     * @param method The method
     * @param shortName The short name that should be used instead of 
     * the full method string. May be <code>null</code>.
     */
    MethodModuleCreator(Method method, String shortName)
    {
        super(ModuleCreatorInstantiator.createInstantiationString(
                method, shortName),
            ModuleInfos.createForMethod(method, shortName));
        this.method = method;
    }

    @Override
    public Module createModule()
    {
        return Modules.createForMethod(getModuleInfo(), method);
    }

}
