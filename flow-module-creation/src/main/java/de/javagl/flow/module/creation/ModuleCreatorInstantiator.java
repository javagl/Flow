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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        "de.javagl.flow.module.creation.ModuleCreatorInstantiator";
    
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
            + "(" + quote(moduleCreatorClass.getName()) + ")";
    }
    
    /**
     * Create the {@link ModuleCreator#getInstantiationString() 
     * instantiation string} for the given {@link ModuleCreator} 
     * class whose constructor receives the given argument.
     * 
     * @param moduleCreatorClass The {@link ModuleCreator} class
     * @param constructorArgument The constructor argument
     * @return The instantiation string
     */
    static String createInstantiationString(
        Class<? extends ModuleCreator> moduleCreatorClass, 
        String constructorArgument)
    {
        return BY_MODULE_CREATOR_CLASS_NAME 
            + "(" + quote(moduleCreatorClass.getName())  
            + ", " + quote(constructorArgument) + ")";
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
            + "(" + quote(method.toGenericString()) 
            + ", " + quote(shortName) + ")";
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
            + "(" + quote(constructor.toGenericString()) + ")";
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
            return createFromModuleCreatorClassName(instantiationString);
        }
        
        if (instantiationString.startsWith(BY_METHOD_STRING))
        {
            return createFromMethodString(instantiationString);
        }

        if (instantiationString.startsWith(BY_CONSTRUCTOR_STRING))
        {
            return createFromConstructorString(instantiationString);
        }

        throw new IllegalArgumentException(
            "Invalid instantiation string: '" + instantiationString + "'");
    }


    /**
     * Create a {@link ModuleCreator} from the given instantiation string,
     * which was created with {@link #createInstantiationString(Class)}
     * 
     * @param instantiationString The instantiation string
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the given string is not a valid
     * instantiation string
     */
    private static ModuleCreator createFromModuleCreatorClassName(
        String instantiationString)
    {
        String parameterString = 
            extractParameterString(instantiationString);
        if (parameterString == null)
        {
            throw new IllegalArgumentException(
                "Invalid instantiation string for instantiation " 
                + "by module creator class name: "
                + "'" + instantiationString + "'");
        }
        
        String[] tokens = tokenize(parameterString);
        if (tokens.length == 1)
        {
            String moduleCreatorClassName = tokens[0]; 
            return instantiateByModuleCreatorClassName(moduleCreatorClassName);
        }
        String moduleCreatorClassName = tokens[0]; 
        String constructorArgument = tokens[1];
        return instantiateByModuleCreatorClassName(
            moduleCreatorClassName, constructorArgument);
    }
    
    /**
     * Create a {@link ModuleCreator} from the given instantiation string,
     * which was created with {@link #createInstantiationString(Method, String)}
     * 
     * @param instantiationString The instantiation string
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the given string is not a valid
     * instantiation string
     */
    private static ModuleCreator createFromMethodString(
        String instantiationString)
    {
        String parameterString = 
            extractParameterString(instantiationString);
        if (parameterString == null)
        {
            throw new IllegalArgumentException(
                "Invalid instantiation string for instantiation " 
                + "by method string: "
                + "'" + instantiationString + "'");
        }
        String tokens[] = tokenize(parameterString);
        
        String methodString = tokens[0];
        String shortNameString = null;
        if (tokens.length > 1)
        {
            shortNameString = tokens[1];
        }
        String shortName = null;
        if (!shortNameString.equals("null"))
        {
            shortName = shortNameString;
        }
        ModuleCreator moduleCreator =  
            instantiateByMethodString(methodString, shortName);
        return moduleCreator;
    }
    
    /**
     * Create a {@link ModuleCreator} from the given instantiation string,
     * which was created with {@link #createInstantiationString(Constructor)}
     * 
     * @param instantiationString The instantiation string
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the given string is not a valid
     * instantiation string
     */
    private static ModuleCreator createFromConstructorString(
        String instantiationString)
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
        int closingIndex = string.lastIndexOf(")");
        if (openingIndex == -1 || 
            closingIndex == -1 ||
            openingIndex == closingIndex)
        {
            return null;
        }
        String result = string.substring(openingIndex + 1, closingIndex);
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
     * Create a {@link ModuleCreator} instance from the given (fully qualified)
     * name of class that implements the {@link ModuleCreator} interface,
     * by calling its constructor that receives the given parameter as its
     * only argument.
     * 
     * @param moduleCreatorClassName The {@link ModuleCreator} class name
     * @param constructorArgument The constructor argument
     * @return The {@link ModuleCreator}
     * @throws IllegalArgumentException If the instantiation was not possible
     */
    private static ModuleCreator instantiateByModuleCreatorClassName(
        String moduleCreatorClassName, String constructorArgument)
    {
        try
        {
            Class<?> c = Classes.forNameUnchecked(moduleCreatorClassName);
            Constructor<?> constructor = 
                Constructors.getConstructorUnchecked(c, String.class);
            Object instance = Constructors.newInstanceUnchecked(
                constructor, constructorArgument);
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
     * Enclose the given string in <code>"</code> quotes
     * 
     * @param string The string 
     * @return The quoted string
     */
    private static String quote(String string)
    {
        return "\"" + string + "\"";
    }
    
    /**
     * Tokenize the given parameter string. This will split the string
     * which consists of comma-separated (<code>,</code>), quoted
     * (<code>"</code>) parts, and return the tokens. Each token is
     * the contents of one quoted string part. So the string<br>
     * <code>"Example", "Yet, another"</code><br>
     * will be converted into the tokens
     * <code>Example</code> and <code>Yet, another</code><br>
     * 
     * @param parameterString The parameter string
     * @return The tokens
     */
    private static String[] tokenize(String parameterString)
    {
        List<String> tokens = new ArrayList<String>();
        String regex = "\"([^\"]*)\"|(,)";
        Matcher m = Pattern.compile(regex).matcher(parameterString);
        while (m.find())
        {
            if (m.group(1) != null)
            {
                tokens.add(m.group(1));
            }
        }
        return tokens.toArray(new String[0]);
    }

    
    /**
     * Private constructor to prevent instantiation
     */
    private ModuleCreatorInstantiator()
    {
        // Private constructor to prevent instantiation
    }
}
