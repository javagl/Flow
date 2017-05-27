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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.Modules;
import de.javagl.reflection.Constructors;
import de.javagl.reflection.Methods;
import de.javagl.reflection.ReflectionException;

/**
 * Utility methods for creating {@link ModuleCreator} instances. <br>
 */
public class ModuleCreators
{
    /**
     * Create a {@link ModuleCreator} that will use the given supplier to
     * create the {@link Module} instances
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param moduleSupplier The supplier for the {@link Module} instances
     * @return The {@link ModuleCreator}
     */
    public static ModuleCreator createForModuleSupplier(
        ModuleInfo moduleInfo, Supplier<? extends Module> moduleSupplier)
    {
        Objects.requireNonNull(moduleSupplier, 
            "The moduleSupplier may not be null");
        return new AbstractModuleCreator(moduleInfo)
        {
            @Override
            public Module createModule()
            {
                return moduleSupplier.get();
            }
        };
    }
    
    /**
     * Create a {@link ModuleCreator} that creates {@link Module} instances
     * based on the given supplier
     * 
     * @param <T> The supplied type
     * 
     * @param name The name 
     * @param description The description
     * @param supplier The supplier
     * @param type The supplied type
     * @return The {@link ModuleCreator}
     */
    public static <T> ModuleCreator createForSupplier(
        String name, String description, Supplier<T> supplier, Class<?> type)
    {
        ModuleInfo moduleInfo = 
            ModuleInfos.createForSupplier(name, description, type);
        return createForModuleSupplier(moduleInfo, 
            () -> Modules.createForSupplier(moduleInfo, supplier));
    }

    /**
     * Create a {@link ModuleCreator} that creates {@link Module} instances
     * based on the given consumer
     * 
     * @param <T> The consumed type 
     * 
     * @param name The name 
     * @param description The description
     * @param consumer The consumer
     * @param type The consumed type
     * @return The {@link ModuleCreator}
     */
    public static <T> ModuleCreator createForConsumer(
        String name, String description, Consumer<T> consumer, Class<?> type)
    {
        ModuleInfo moduleInfo = 
            ModuleInfos.createForConsumer(name, description, type);
        return createForModuleSupplier(moduleInfo, 
            () -> Modules.createForConsumer(moduleInfo, consumer));
    }
    
    /**
     * Create a {@link ModuleCreator} that creates {@link Module} instances
     * based on the given function
     * 
     * @param <S> The argument type of the function
     * @param <T> The return type of the function
     * 
     * @param name The name 
     * @param description The description
     * @param function The function
     * @param argumentType The argument type of the function
     * @param resultType The result type of the function
     * @return The {@link ModuleCreator}
     */
    public static <S, T> ModuleCreator createForFunction(
        String name, String description, Function<S, T> function,
        Class<?> argumentType, Class<?> resultType)
    {
        ModuleInfo moduleInfo = 
            ModuleInfos.createForFunction(
                name, description, argumentType, resultType);
        return createForModuleSupplier(moduleInfo, 
            () -> Modules.createForFunction(moduleInfo, function));
    }
    
    
    /**
     * Create a {@link ModuleCreator} that will create {@link Module} instances
     * that call the given method
     * 
     * @param method The method
     * @return The {@link ModuleCreator}
     */
    public static ModuleCreator createForMethod(Method method)
    {
        return createForMethod(method, null);
    }
    
    /**
     * Create a {@link ModuleCreator} that will create {@link Module} instances
     * that call the given method
     * 
     * @param method The method
     * @param shortName An optional short name for the module description
     * @return The {@link ModuleCreator}
     */
    public static ModuleCreator createForMethod(
        Method method, String shortName)
    {
        return new MethodModuleCreator(method, shortName);
    }
    
    
    /**
     * Create a {@link ModuleCreator} that will create {@link Module} instances
     * that call the specified method.
     * 
     * @param shortName A short name that should be used as the name 
     * instead of the full method string. May be <code>null</code>.
     * @param declaringClass The class that declares the method
     * @param methodName The name of the method
     * @param parameterTypes The parameter types of the method
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the method cannot be obtained
     */
    public static ModuleCreator createForMethod(
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
     * Create a {@link ModuleCreator} that will create {@link Module} instances
     * that will create instances using the given constructor
     * 
     * @param constructor The constructor
     * @return The {@link ModuleCreator}
     */
    public static ModuleCreator createForConstructor(
        Constructor<?> constructor)
    {
        return new ConstructorModuleCreator(constructor);
    }
    
    /**
     * Create a {@link ModuleCreator} that will create {@link Module} instances
     * that will create instances using the default constructor of the given 
     * class.
     * 
     * @param c The class
     * @return The {@link ModuleCreator}
     */
    public static ModuleCreator createForConstructor(Class<?> c)
    {
        Constructor<?> constructor = 
            Constructors.getDeclaredConstructorOptional(c);
        if (constructor == null)
        {
            throw new IllegalArgumentException(
                "Class " + c + " does not have a valid default constructor");
        }
        return new ConstructorModuleCreator(constructor);
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private ModuleCreators()
    {
        // Private constructor to prevent instantiation
    }

    
    
}
