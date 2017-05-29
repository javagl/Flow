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

import de.javagl.category.Categories;
import de.javagl.category.CategoriesBuilder;
import de.javagl.category.Category;
import de.javagl.flow.module.creation.ModuleCreator;

/**
 * Abstract base implementation of a simple {@link ModuleCreatorSource}. 
 * It offers a single abstract method {@link #addModuleCreators()}, which may 
 * be overridden to collect the {@link ModuleCreator} instances that should
 * be provided by this source.
 */
public abstract class AbstractModuleCreatorSource implements ModuleCreatorSource
{
    /**
     * The {@link CategoriesBuilder} that is used to build the 
     * {@link Category} of {@link ModuleCreator} instances
     */
    private final CategoriesBuilder<ModuleCreator> categoriesBuilder;
    
    /**
     * Creates a new source for {@link ModuleCreator} instances that belong 
     * to a {@link Category} hierarchy where the root {@link Category} 
     * has the given name
     * 
     * @param categoryName The name of the root {@link Category}
     */
    protected AbstractModuleCreatorSource(String categoryName)
    {
        categoriesBuilder = Categories.createBuilder(categoryName);
        addModuleCreators();
    }

    /**
     * Add all {@link ModuleCreator} instances that should be provided by
     * this source. An example implementation of this method might look
     * like this:
     * <pre><code>
     * protected void addModuleCreators()
     * {
     *   add(new ImageLoaderModuleCreator());
     *   add(new ImageViewerModuleCreator());
     *   
     *   get("Filter").add(new ImageEdgeDetectorModuleCreator()); 
     *   get("Filter").add(new ImageEmbossModuleCreator()); 
     * }
     * </code></pre>
     */
    protected abstract void addModuleCreators();

    /**
     * Add the given {@link ModuleCreator} to this source
     * 
     * @param moduleCreator The {@link ModuleCreator} to add
     */
    protected final void add(ModuleCreator moduleCreator)
    {
        categoriesBuilder.add(moduleCreator);
    }
    
    /**
     * Returns a {@link CategoriesBuilder} for a sub-{@link Category} with 
     * the given name
     * 
     * @param subCategoryName The name of the sub-{@link Category}
     * @return The {@link CategoriesBuilder} for the sub-{@link Category}
     */
    protected final CategoriesBuilder<ModuleCreator> get(String subCategoryName)
    {
        return categoriesBuilder.get(subCategoryName);
    }
    
    /**
     * Returns the {@link CategoriesBuilder} for the root category that 
     * is currently built. 
     * 
     * @return The {@link CategoriesBuilder} for the current {@link Category}
     */
    protected final CategoriesBuilder<ModuleCreator> get()
    {
        return categoriesBuilder;
    }
    

    @Override
    public final Category<ModuleCreator> getCategory()
    {
        return categoriesBuilder.get();
    }
    
}
