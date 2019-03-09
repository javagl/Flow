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
package de.javagl.flow.gui.editor;

import static de.javagl.flow.module.creation.ConstructorModuleCreators.createConstructorModuleCreators;
import static de.javagl.flow.module.creation.MethodModuleCreators.createStaticMethodModuleCreators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import de.javagl.category.Categories;
import de.javagl.category.CategoriesBuilder;
import de.javagl.category.Category;
import de.javagl.flow.module.creation.ModuleCreator;

/**
 * A class that provides a {@link Category} of  {@link ModuleCreator} 
 * instances for "built-in" operations
 */
class BuiltInModuleCreators
{
    /**
     * Create the {@link Category} containing the {@link ModuleCreator} 
     * instances for built-in operations
     * 
     * @return The {@link ModuleCreator} {@link Category}
     */
    static Category<ModuleCreator> createBuiltInModuleCreators()
    {
        CategoriesBuilder<ModuleCreator> b = 
            Categories.createBuilder("Built-in");
        
        b.get("Constructors").get("ArrayList").addAll(
            createConstructorModuleCreators(ArrayList.class));
        b.get("Constructors").get("LinkedHashSet").addAll(
            createConstructorModuleCreators(LinkedHashSet.class));
        b.get("Constructors").get("LinkedHashMap").addAll(
            createConstructorModuleCreators(LinkedHashMap.class));
        
        b.get("Methods").get("Collections").addAll(
            createStaticMethodModuleCreators(Collections.class));
        b.get("Methods").get("Arrays").addAll(
            createStaticMethodModuleCreators(Arrays.class));
        b.get("Methods").get("Comparator").addAll(
            createStaticMethodModuleCreators(Comparator.class));
        
        return b.get();
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private BuiltInModuleCreators()
    {
        // Private constructor to prevent instantiation
    }

}
