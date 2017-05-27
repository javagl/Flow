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

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import de.javagl.reflection.Constructors;
import de.javagl.types.Types;

/**
 * A {@link Module} that wraps a single constructor call.
 */
final class ConstructorModule extends SimpleAbstractModule
{
    /**
     * The constructor that is called
     */
    private final Constructor<?> constructor;
    
    /**
     * Creates a new constructor calling {@link Module} with
     * the given {@link ModuleInfo} that calls the given
     * constructor.
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param constructor The constructor to call
     */
    ConstructorModule(ModuleInfo moduleInfo, Constructor<?> constructor)
    {
        super(moduleInfo);
        this.constructor = constructor;
    }

    @Override
    protected void process()
    {
        // Obtain the constructor arguments from the input slots
        int numArguments = getModuleInfo().getInputSlotInfos().size();
        Object arguments[] = new Object[numArguments];
        for (int i=0; i<numArguments; i++)
        {
            arguments[i] =  obtainInput(i);
        }
        
        // Invoke the constructor
        fireBeforeProcessing();
        Object result = 
            Constructors.newInstanceNonAccessibleUnchecked(
                constructor, arguments);
        fireAfterProcessing();
        
        // Forward the instance
        forwardOutput(0, result);

        // Forward the parameters
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        int n = 1;
        for (int i=0; i<numArguments; i++)
        {
            if (!Types.isPrimitive(parameterTypes[i]))
            {
                forwardOutput(n, arguments[i]);
                n++;
            }
        }
    }
}