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
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.reflection.Constructors;
import de.javagl.reflection.Methods;
import de.javagl.reflection.ReflectionException;

/**
 * Utility methods to create {@link Module} instances. 
 */
public class Modules
{
    /**
     * Creates a new {@link Module} that creates instances of the given 
     * class by invoking its parameterless constructor.
     * 
     * @param instantiatedClass The instantiated class
     * @return The new {@link Module}
     * @throws IllegalArgumentException If the constructor from the given
     * class cannot be obtained
     */
    public static Module createForConstructor(Class<?> instantiatedClass)
    {
        Constructor<?> constructor = 
            Constructors.getDeclaredConstructorOptional(instantiatedClass);
        if (constructor == null)
        {
            throw new IllegalArgumentException(
                "Could not obtain constructor from class " + instantiatedClass);
        }
        
        return createForConstructor(
            ModuleInfos.createForConstructor(constructor),
            instantiatedClass);
    }

    /**
     * Creates a new {@link Module} with the given {@link ModuleInfo}
     * that creates instances of the given class by invoking its 
     * parameterless constructor.
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param instantiatedClass The instantiated class
     * @return The new {@link Module}
     * @throws IllegalArgumentException If the constructor from the given
     * class cannot be obtained
     */
    public static Module createForConstructor(
        ModuleInfo moduleInfo, Class<?> instantiatedClass)
    {
        Constructor<?> constructor = 
            Constructors.getConstructorOptional(instantiatedClass);
        if (constructor == null)
        {
            throw new IllegalArgumentException(
                "Could not obtain constructor from class " + instantiatedClass);
        }
        return createForConstructor(moduleInfo, constructor);
    }
    
    /**
     * Creates a new {@link Module} with the given {@link ModuleInfo}
     * that calls the given (parameterless) constructor
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param constructor The constructor
     * @return The new {@link Module}
     */
    public static Module createForConstructor(
        ModuleInfo moduleInfo, Constructor<?> constructor)
    {
        return new ConstructorModule(moduleInfo, constructor);
    }
    
    
    /**
     * Creates a new {@link Module} with the given name and description
     * that internally only calls the {@link Supplier#get()} 
     * method of the given {@link Supplier} and forwards the result to 
     * the first {@link OutputSlot}.
     * 
     * @param <T> The supplied type
     * 
     * @param name The name 
     * @param description The description
     * @param supplier The {@link Supplier}
     * @param type The supplied type
     * @return The new {@link Module}
     */
    public static <T> Module createForSupplier(
        String name, String description, Supplier<T> supplier, Class<?> type)
    {
        ModuleInfo moduleInfo = 
            ModuleInfos.createForSupplier(name, description, type);
        return createForSupplier(moduleInfo, supplier);
    }
    
    /**
     * Creates a new {@link Module} with the given {@link ModuleInfo}
     * that internally only calls the {@link Supplier#get()} 
     * method of the given {@link Supplier} and forwards the result to 
     * the first {@link OutputSlot}.
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param supplier The {@link Supplier}
     * @return The new {@link Module}
     */
    public static Module createForSupplier(
        ModuleInfo moduleInfo, Supplier<?> supplier)
    {
        Module result = new SimpleAbstractModule(moduleInfo)
        {
            @Override
            protected void processCore(Object inputs[], Object outputs[])
            {
                outputs[0] = supplier.get();
            }
        };
        return result;
    }

    /**
     * Creates a new {@link Module} with the given name and description
     * that internally only calls the {@link Consumer#accept(Object)} 
     * method of the given {@link Consumer} with the value that has been 
     * obtained from the first {@link InputSlot}.
     * 
     * @param <T> The consumed type
     * 
     * @param name The name 
     * @param description The description
     * @param consumer The {@link Consumer}
     * @param type The consumed type
     * @return The new {@link Module}
     */
    public static <T> Module createForConsumer(
        String name, String description, Consumer<T> consumer, Class<?> type)
    {
        ModuleInfo moduleInfo = 
            ModuleInfos.createForConsumer(name, description, type);
        return createForConsumer(moduleInfo, consumer);
    }
    
    /**
     * Creates a new {@link Module} with the given {@link ModuleInfo}
     * that internally only calls the {@link Consumer#accept(Object)} 
     * method of the given {@link Consumer} with the value that has been 
     * obtained from the first {@link InputSlot}.
     * 
     * @param <T> The consumed type
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param consumer The {@link Consumer}
     * @return The new {@link Module}
     */
    public static <T> Module createForConsumer(
        ModuleInfo moduleInfo, Consumer<T> consumer)
    {
        Module result = new SimpleAbstractModule(moduleInfo)
        {
            @Override
            protected void processCore(Object inputs[], Object outputs[])
            {
                @SuppressWarnings("unchecked")
                T value = (T) inputs[0];
                consumer.accept(value);
            }
        };
        return result;
    }

    /**
     * Creates a new {@link Module} with the given name and description
     * that internally only calls the <code>Function#apply</code> 
     * method of the given function with the value that has been 
     * obtained from the first {@link InputSlot}, and forwards the
     * result to the first {@link OutputSlot}.
     * 
     * @param <S> The argument type of the function
     * @param <T> The result type of the function
     * 
     * @param name The name 
     * @param description The description
     * @param function The {@link Function}
     * @param argumentType The argument type of the function
     * @param resultType The result type of the function
     * @return The new {@link Module}
     */
    public static <S, T> Module createForFunction(
        String name, String description, Function<S, T> function,
        Class<?> argumentType, Class<?> resultType)
    {
        ModuleInfo moduleInfo = 
            ModuleInfos.createForFunction(
                name, description, argumentType, resultType);
        return createForFunction(moduleInfo, function);
    }
    
    /**
     * Creates a new {@link Module} with the given {@link ModuleInfo}
     * that internally only calls the {@link Function#apply(Object)} 
     * method of the given {@link Function} with the value that has 
     * been obtained from the first {@link InputSlot}, and forwards the
     * result to the first {@link OutputSlot}.
     * 
     * @param <S> The source type of the {@link Function}
     * @param <T> The target type of the {@link Function}
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param function The {@link Function}
     * @return The new {@link Module}
     */
    public static <S, T> Module createForFunction(
        ModuleInfo moduleInfo, Function<S, T> function)
    {
        Module result = new SimpleAbstractModule(moduleInfo)
        {
            @Override
            protected void processCore(Object inputs[], Object outputs[])
            {
                @SuppressWarnings("unchecked")
                S inputValue = (S)inputs[0];
                T outputValue = function.apply(inputValue);
                outputs[0] = outputValue;
            }
        };
        return result;
    }
    
    /**
     * Create a {@link Module} that calls the given method.
     * 
     * @param method The method
     * @return The {@link Module}
     */
    public static Module createForMethod(Method method)
    {
        return createForMethod(method, null);
    }
    
    /**
     * Create a {@link Module} that calls the specified method.
     * 
     * @param shortName A short name that should be used as the name 
     * instead of the full method string. May be <code>null</code>.
     * @param method The method
     * @return The {@link Module}
     */
    public static Module createForMethod(Method method, String shortName)
    {
        return createForMethod(
            ModuleInfos.createForMethod(method, shortName), method);
    }
    
    /**
     * Create a {@link Module} that calls the given method.
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param method The method
     * @return The {@link Module}
     */
    public static Module createForMethod(ModuleInfo moduleInfo, Method method)
    {
        return new MethodModule(moduleInfo, method);
    }
    
    
    /**
     * Create a {@link Module} that calls the specified method.
     * 
     * @param shortName A short name that should be used as the name 
     * instead of the full method string. May be <code>null</code>.
     * @param declaringClass The class that declares the method
     * @param methodName The name of the method
     * @param parameterTypes The parameter types of the method
     * @return The {@link Module}
     * @throws IllegalArgumentException If the method cannot be obtained
     */
    public static Module createForMethod(
        String shortName, Class<?> declaringClass, 
        String methodName, Class<?> ... parameterTypes)
    {
        Method method = null;
        try
        {
            method = Methods.getDeclaredMethodUnchecked(
                declaringClass, methodName, parameterTypes);
        }
        catch (ReflectionException e)
        {
            throw new IllegalArgumentException(e);
        }
        return createForMethod(method, shortName);
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private Modules()
    {
        // Private constructor to prevent instantiation
    }
    
}
