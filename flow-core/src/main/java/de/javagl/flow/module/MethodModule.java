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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import de.javagl.reflection.Methods;
import de.javagl.types.Types;

/**
 * A {@link Module} that wraps a single method call.
 */
final class MethodModule extends SimpleAbstractModule
{
    /**
     * The method that is called
     */
    private final Method method;
    
    /**
     * Creates a new method calling {@link Module} with
     * the given {@link ModuleInfo} that calls the given
     * method.
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param method The method to call
     */
    MethodModule(ModuleInfo moduleInfo, Method method)
    {
        super(moduleInfo);
        this.method = method;
    }

    @Override
    protected void process()
    {
        // When the method is an instance method, obtain the
        // instance from the first input slot
        int inputOffset = 0;
        Object instance = null;
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (!isStatic)
        {
            instance = obtainInput(0);
            inputOffset++;
        }
        
        // Obtain the method arguments from the remaining input slots
        Type[] parameterTypes = method.getGenericParameterTypes();
        int numArguments = parameterTypes.length;
        Object arguments[] = new Object[numArguments];
        for (int i = 0; i < numArguments; i++)
        {
            arguments[i] = obtainInput(i + inputOffset);
        }
        
        // Invoke the method
        fireBeforeProcessing();
        Object result = null;
        try
        {
            result = Methods.invokeUnchecked(
                method, instance, arguments);
        }
        finally
        {
            fireAfterProcessing();
        }
        
        // Forward the instance and return value, and the non-primitive
        // method arguments
        int n = 0;
        if (!isStatic)
        {
            forwardOutput(n, instance);
            n++;
        }
        Type returnType = method.getGenericReturnType();
        if (!Types.isVoid(returnType))
        {
            forwardOutput(n, result);
            n++;
        }
        for (int i = 0; i < numArguments; i++)
        {
            if (!Types.isPrimitive(parameterTypes[i]))
            {
                forwardOutput(n, arguments[i]);
                n++;
            }
        }
    }
}