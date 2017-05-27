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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.javagl.category.Category;
import de.javagl.category.MutableCategory;
import de.javagl.flow.module.Module;
import de.javagl.reflection.Members;
import de.javagl.reflection.Methods;

/**
 * Methods to create {@link ModuleCreator} instances that can create modules
 * that call methods of a given type.
 */
public class MethodModuleCreators
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(MethodModuleCreators.class.getName());
    
    /**
     * Add all {@link ModuleCreator} objects that can call instance methods 
     * on the given type to the given {@link Category}. This method will be 
     * called recursively on the supertype of the given type and its
     * implemented interfaces, and the {@link ModuleCreator} objects of these 
     * recursive calls will be added to child {@link Category Categories}
     * that are added to the given {@link Category}. 
     * 
     * @param category The {@link Category}
     * @param type The type to call methods on
     */
    public static void addInstanceMethodModuleCreators(
        MutableCategory<ModuleCreator> category, Type type)
    {
        if (type instanceof Class<?>)
        {
            Class<?> c = (Class<?>)type;
            List<Method> methods = Methods.getOwnMethodsOptional(c);
            List<Method> instanceMethods = methods.stream()
                .filter(Members::isNotStatic)
                .collect(Collectors.toList());
            Set<ModuleCreator> instanceMethodModuleCreators = 
                createMethodModuleCreators(instanceMethods);
            category.addElements(instanceMethodModuleCreators);
            
            Class<?> superclass = c.getSuperclass();
            if (superclass != null)
            {
                MutableCategory<ModuleCreator> superclassCategory = 
                    category.addChild(String.valueOf(superclass));
                addInstanceMethodModuleCreators(
                    superclassCategory, superclass);
            }
            for (Class<?> i : c.getInterfaces())
            {
                MutableCategory<ModuleCreator> interfaceCategory = 
                    category.addChild(String.valueOf(i));
                addInstanceMethodModuleCreators(interfaceCategory, i);
            }
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            addInstanceMethodModuleCreators(category, rawType);
        }
        else if (type instanceof WildcardType)
        {
            WildcardType wildcardType = (WildcardType)type;
            for (Type bound : wildcardType.getUpperBounds())
            {
                MutableCategory<ModuleCreator> boundCategory = 
                    category.addChild(String.valueOf(bound));
                addInstanceMethodModuleCreators(boundCategory, bound);
            }
        }
        else if (type instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable = (TypeVariable<?>)type;
            for (Type bound : typeVariable.getBounds())
            {
                MutableCategory<ModuleCreator> boundCategory = 
                    category.addChild(String.valueOf(bound));
                addInstanceMethodModuleCreators(boundCategory, bound);
            }
        }
        else if (type instanceof GenericArrayType)
        {
            // No methods to call here
        }
        else
        {
            logger.warning("Unknown type: " + type 
                + ", type of type is " + type.getClass());
        }
    }
    
    
    /**
     * Add all {@link ModuleCreator} objects that can call public static 
     * methods on the given type to the given {@link Category}. 
     * 
     * @param category The {@link Category}
     * @param type The type to call methods on
     */
    public static void addStaticMethodModuleCreators(
        MutableCategory<ModuleCreator> category, Class<?> type)
    {
        category.addElements(createStaticMethodModuleCreators(type));
    }
    
    /**
     * Create a set of {@link ModuleCreator} objects that can create
     * {@link Module} instances that call public static methods of
     * the given type. 
     * 
     * @param type The type to call methods on
     * @return The set of {@link ModuleCreator} instances
     */
    public static Set<ModuleCreator> createStaticMethodModuleCreators(
        Class<?> type)
    {
        List<Method> methods = Methods.getAllOptional(type,
            Members::isStatic,
            Members::isPublic,
            m -> m.getDeclaringClass().equals(type));
        Set<ModuleCreator> methodModuleCreators = 
            createMethodModuleCreators(methods);
        return methodModuleCreators;
    }
    

    /**
     * Creates a set of {@link ModuleCreator} instances that create 
     * {@link Module} instances for calling the given methods
     *  
     * @param methods The methods to call
     * @return The set of {@link ModuleCreator} instances
     */
    private static Set<ModuleCreator> createMethodModuleCreators(
        Iterable<? extends Method> methods)
    {
        Set<ModuleCreator> moduleCreators = new LinkedHashSet<ModuleCreator>();
        for (Method method : methods)
        {
            ModuleCreator moduleCreator = 
                ModuleCreators.createForMethod(method);
            moduleCreators.add(moduleCreator);
        }
        return moduleCreators;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private MethodModuleCreators()
    {
        // Private constructor to prevent instantiation 
    }
}
