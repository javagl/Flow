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
package de.javagl.flow.plugin;

import java.util.List;

import de.javagl.category.CategoriesBuilder;
import de.javagl.category.Category;
import de.javagl.flow.module.creation.ModuleCreator;

/**
 * Methods for discovering {@link ModuleCreatorSource} implementations and
 * adding their outputs to a {@link CategoriesBuilder}
 */
public class ModuleCreatorServices
{
    /**
     * Find all {@link ModuleCreatorSource} implementations that are offered
     * as service implementations in the classpath, and merge their
     * {@link ModuleCreatorSource#getCategory() category} with the 
     * one that is created by the given {@link CategoriesBuilder}.
     *  
     * @param categoriesBuilder The target {@link CategoriesBuilder}
     */
    public static void initialize(
        CategoriesBuilder<ModuleCreator> categoriesBuilder)
    {
        List<ModuleCreatorSource> moduleCreatorSources = 
            ModuleCreatorService.createModuleCreatorSources();
        for (ModuleCreatorSource moduleCreatorSource : moduleCreatorSources)
        {
            Category<ModuleCreator> category = 
                moduleCreatorSource.getCategory();
            categoriesBuilder.get(category.getName())
                .mergeRecursively(category);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private ModuleCreatorServices()
    {
        // Private constructor to prevent instantiation
    }
}
