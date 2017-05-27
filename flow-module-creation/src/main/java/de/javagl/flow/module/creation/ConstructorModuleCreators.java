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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.javagl.category.Category;
import de.javagl.category.MutableCategory;
import de.javagl.flow.module.Module;
import de.javagl.reflection.Constructors;

/**
 * Methods to create {@link ModuleCreator} instances that can create modules
 * that call constructors
 */
public class ConstructorModuleCreators
{
    /**
     * Add all {@link ModuleCreator} objects that can create instances of
     * the given type to the given {@link Category}.  
     * 
     * @param category The {@link Category}
     * @param type The type to call constructors on
     */
    public static void addConstructorModuleCreators(
        MutableCategory<ModuleCreator> category, Class<?> type)
    {
        category.addElements(createConstructorModuleCreators(type));
    }

    /**
     * Create the set of {@link ModuleCreator} objects that can create 
     * {@link Module} instances that create instances of the given type
     * by calling one of its public constructors.
     * 
     * @param type The type to call constructors on
     * @return The set of {@link ModuleCreator} objects
     */
    public static Set<ModuleCreator> createConstructorModuleCreators(
        Class<?> type)
    {
        List<Constructor<?>> constructors = 
            Constructors.getConstructorsOptional(type);
        Set<ModuleCreator> constructorModuleCreators = 
            createConstructorModuleCreators(constructors);
        return constructorModuleCreators;
    }
    
    /**
     * Creates a set of {@link ModuleCreator} instances that create 
     * {@link Module} instances for calling the given constructors
     *  
     * @param constructors The constructors to call
     * @return The set of {@link ModuleCreator} instances
     */
    private static Set<ModuleCreator> createConstructorModuleCreators(
        Iterable<? extends Constructor<?>> constructors)
    {
        Set<ModuleCreator> moduleCreators = new LinkedHashSet<ModuleCreator>();
        for (Constructor<?> constructor : constructors)
        {
            ModuleCreator moduleCreator = 
                ModuleCreators.createForConstructor(constructor);
            moduleCreators.add(moduleCreator);
        }
        return moduleCreators;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private ConstructorModuleCreators()
    {
        // Private constructor to prevent instantiation 
    }
}
