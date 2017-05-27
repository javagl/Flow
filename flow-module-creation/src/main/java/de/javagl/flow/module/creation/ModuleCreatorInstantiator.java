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

import de.javagl.flow.module.Module;
import de.javagl.reflection.Classes;
import de.javagl.reflection.Constructors;
import de.javagl.reflection.Methods;
import de.javagl.reflection.ReflectionException;

/**
 * A class to create {@link ModuleCreator} instances based on the
 * {@link ModuleCreator#getInstantiationString() instantiation string}.
 * This is only used for the persistence, and should not be used by clients.
 */
public final class ModuleCreatorInstantiator
{
    /**
     * The full, qualified name of this class
     */
    private static final String THIS_CLASS_NAME = 
        "de.javagl.flow.ModuleCreatorInstantiator";
    
    /**
     * The prefix that indicates that a {@link ModuleCreator} should
     * be instantiated based on its class name
     */
    private static final String BY_MODULE_CREATOR_CLASS_NAME = 
        THIS_CLASS_NAME + ".instantiateByModuleCreatorClassName"; 
    
    /**
     * The prefix that indicates that a {@link ModuleCreator} should
     * be instantiated based on a full method string
     */
    private static final String BY_METHOD_STRING = 
        THIS_CLASS_NAME + ".instantiateByMethodString"; 

    /**
     * The prefix that indicates that a {@link ModuleCreator} should
     * be instantiated based on a full constructor string
     */
    private static final String BY_CONSTRUCTOR_STRING = 
        THIS_CLASS_NAME + ".instantiateByConstructorString"; 

    
    /**
     * Create the {@link ModuleCreator#getInstantiationString() 
     * instantiation string} for the given {@link ModuleCreator} class
     * 
     * @param moduleCreatorClass The {@link ModuleCreator} class
     * @return The instantiation string
     */
    static String createInstantiationString(
        Class<? extends ModuleCreator> moduleCreatorClass)
    {
        return BY_MODULE_CREATOR_CLASS_NAME 
            + "(" + moduleCreatorClass.getName() + ")";
    }

    /**
     * Create the  {@link ModuleCreator#getInstantiationString() 
     * instantiation string} for a {@link ModuleCreator} that creates
     * {@link Module} instances that call the given method
     * 
     * @param method The method
     * @param shortName An optional short name for the modules
     * @return The instantiation string
     */
    static String createInstantiationString(Method method, String shortName)
    {
        return BY_METHOD_STRING 
            + "(" + method.toGenericString() + ", " + shortName + ")";
    }
    
    /**
     * Create the  {@link ModuleCreator#getInstantiationString() 
     * instantiation string} for a {@link ModuleCreator} that creates
     * {@link Module} instances that create instances by calling the
     * the given constructor
     * 
     * @param constructor The constructor
     * @return The instantiation string
     */
    static String createInstantiationString(Constructor<?> constructor)
    {
        return BY_CONSTRUCTOR_STRING 
            + "(" + constructor.toGenericString() + ")";
    }
    
    
    /**
     * Instantiate a {@link ModuleCreator} based on the given 
     * {@link ModuleCreator#getInstantiationString() instantiation string}. 
     * <br>
     * <br> 
     * The instantiation string may be a string containing ...
     * <ul>
     *   <li>
     *     the class name of the {@link ModuleCreator} 
     *     implementation, which is created using the  
     *     {@link #createInstantiationString(Class)} 
     *     method
     *   </li> 
     *   <li>
     *     a method string, which is created using the
     *     {@link #createInstantiationString(java.lang.reflect.Method, String)} 
     *     method.
     *   </li> 
     *   <li>
     *     a constructor string, which is created using the
     *     {@link #createInstantiationString(java.lang.reflect.Constructor)} 
     *     method.
     *   </li> 
     * </ul>  
     * 
     * @param instantiationString The instantiation string
     * @return The {@link ModuleCreator} instance
     * @throws IllegalArgumentException If the given string is not a valid
     * instantiation string
     */
    public static ModuleCreator createFromInstantiationString(
        String instantiationString)
    {
        if (instantiationString.startsWith(BY_MODULE_CREATOR_CLASS_NAME))
        {
            String moduleCreatorClassName = 
                extractParameterString(instantiationString);
            if (moduleCreatorClassName == null)
            {
                throw new IllegalArgumentException(
                    "Invalid instantiation string for instantiation " 
                    + "by module creator class name: "
                    + "'" + instantiationString + "'");
            }
            ModuleCreator moduleCreator =  
                instantiateByModuleCreatorClassName(moduleCreatorClassName);
            return moduleCreator;
        }
        
        if (instantiationString.startsWith(BY_METHOD_STRING))
        {
            String methodStringAndShortName = 
                extractParameterString(instantiationString);
            if (methodStringAndShortName == null)
            {
                throw new IllegalArgumentException(
                    "Invalid instantiation string for instantiation " 
                    + "by method string: "
                    + "'" + instantiationString + "'");
            }
            int commaIndex = methodStringAndShortName.lastIndexOf(',');
            if (commaIndex == -1)
            {
                throw new IllegalArgumentException(
                    "Invalid instantiation string for instantiation " 
                    + "by method string: "
                    + "'" + instantiationString + "'");
            }
            String methodString = 
                methodStringAndShortName.substring(0, commaIndex).trim();
            String shortNameString =
                methodStringAndShortName.substring(commaIndex + 1).trim();
            String shortName = null;
            if (!shortNameString.equals("null"))
            {
                shortName = shortNameString;
            }
            ModuleCreator moduleCreator =  
                instantiateByMethodString(methodString, shortName);
            return moduleCreator;
        }

        if (instantiationString.startsWith(BY_CONSTRUCTOR_STRING))
        {
            String constructorString = 
                extractParameterString(instantiationString);
            if (constructorString == null)
            {
                throw new IllegalArgumentException(
                    "Invalid instantiation string for instantiation " 
                    + "by constructor string: "
                    + "'" + instantiationString + "'");
            }
            ModuleCreator moduleCreator =  
                instantiateByConstructorString(constructorString);
            return moduleCreator;
        }

        throw new IllegalArgumentException(
            "Invalid instantiation string: '" + instantiationString + "'");
    }
    
    
    /**
     * Returns the string that is contained in the given string between the
     * first opening <code>(</code> and the last closing </code>)</code> 
     * character
     * 
     * @param string The input string
     * @return The parameter string, or <code>null</code> if the given string
     * does not contain a valid parameter string
     */
    private static String extractParameterString(String string)
    {
        int openingIndex = string.indexOf("(");
        int closingIndex1 = string.lastIndexOf(")");
        if (openingIndex == -1 || closingIndex1 == -1 ||
            openingIndex == closingIndex1)
        {
            return null;
        }
        String result = string.substring(openingIndex + 1, closingIndex1);
        return result;
    }
    
    /**
     * Create a {@link ModuleCreator} instance from the given (fully qualified)
     * name of class that implements the {@link ModuleCreator} interface.
     * 
     * @param moduleCreatorClassName The {@link ModuleCreator} class name
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the instantiation was not possible
     */
    private static ModuleCreator instantiateByModuleCreatorClassName(
        String moduleCreatorClassName)
    {
        try
        {
            Object instance = Classes.newInstanceUnchecked(
                moduleCreatorClassName);
            if (!(instance instanceof ModuleCreator))
            {
                throw new IllegalArgumentException(
                    "Class is not a ModuleCreator: " + moduleCreatorClassName);
            }
            return (ModuleCreator)instance;
        }
        catch (ReflectionException e)
        {
            throw new IllegalArgumentException(
                "Could not instantiate ModuleCreator from class name: "
                + moduleCreatorClassName, e);
        }
    }
    
    /**
     * Create a {@link ModuleCreator} instance from the given string that
     * was created by calling <code>method.toString()</code> or 
     * <code>method.toGenericString()</code> of a method.
     * 
     * @param fullMethodString The method string
     * @param shortName The optional short name for the {@link Module}
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the instantiation was not possible
     */
    private static ModuleCreator instantiateByMethodString(
        String fullMethodString, String shortName)
    {
        try
        {
            Method method = Methods.parseMethodUnchecked(fullMethodString);
            return ModuleCreators.createForMethod(method, shortName);
        }
        catch (ReflectionException e)
        {
            throw new IllegalArgumentException(
                "Could not instantiate ModuleCreator from method string: "
                + fullMethodString, e);
        }
    }

    /**
     * Create a {@link ModuleCreator} instance from the given string that
     * was created by calling <code>constructor.toString()</code> or 
     * <code>constructor.toGenericString()</code> of a constructor.
     * 
     * @param constructorString The constructor string
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the instantiation was not possible
     */
    private static ModuleCreator instantiateByConstructorString(
        String constructorString)
    {
        try
        {
            Constructor<?> constructor = 
                Constructors.parseConstructorUnchecked(constructorString);
            return ModuleCreators.createForConstructor(constructor);
        }
        catch (ReflectionException e)
        {
            throw new IllegalArgumentException(
                "Could not instantiate ModuleCreator from constructor string: "
                + constructorString, e);
        }
    }
    

    /**
     * Private constructor to prevent instantiation
     */
    private ModuleCreatorInstantiator()
    {
        // Private constructor to prevent instantiation
    }
}
