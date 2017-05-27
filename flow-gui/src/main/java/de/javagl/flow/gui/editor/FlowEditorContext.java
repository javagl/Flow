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

import java.util.Set;

import de.javagl.category.Categories;
import de.javagl.category.Category;
import de.javagl.flow.gui.FlowEditorApplication;
import de.javagl.flow.gui.compatibility.ModuleCompatibilityModel;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.repository.LoggingRepositoryListener;
import de.javagl.flow.repository.Repositories;
import de.javagl.flow.repository.Repository;

/**
 * A class describing the context in which a {@link FlowEditorApplication} 
 * may work. It summarizes the {@link Category} hierarchy containing all
 * available {@link ModuleCreator} objects and the {@link Repository} of 
 * all available {@link ModuleCreator} objects
 */
public final class FlowEditorContext
{
    /**
     * The root node of the {@link Category} hierarchy that contains all
     * {@link ModuleCreator} instances (except for the built-in ones) 
     */
    private final Category<ModuleCreator> mainModuleCreatorCategory;
    
    /**
     * The {@link ModuleCompatibilityModel} for the main modules
     */
    private final ModuleCompatibilityModel mainModuleCompatibilityModel;

    /**
     * The root node of the {@link Category} hierarchy that contains all
     * built-in {@link ModuleCreator} instances. 
     */
    private final Category<ModuleCreator> builtInModuleCreatorCategory;

    /**
     * The {@link ModuleCompatibilityModel} for the built-in modules
     */
    private final ModuleCompatibilityModel builtInModuleCompatibilityModel;
    
    /**
     * The {@link Repository} containing all {@link ModuleCreator} objects
     */
    private final Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository;
    
    /**
     * Creates a context containing the given root {@link Category} node
     * with all available {@link ModuleCreator} instances.
     * 
     * @param mainModuleCreatorCategory The root {@link Category}
     * with all available {@link ModuleCreator} instances. 
     */
    public FlowEditorContext(Category<ModuleCreator> mainModuleCreatorCategory)
    {
        this.mainModuleCreatorCategory = mainModuleCreatorCategory;
        this.builtInModuleCreatorCategory = 
            BuiltInModuleCreators.createBuiltInModuleCreators();    

        this.moduleCreatorRepository = 
            Repositories.create(moduleCreator -> moduleCreator.getModuleInfo());
        
        this.moduleCreatorRepository.addRepositoryListener(
            LoggingRepositoryListener.create());
        
        Set<ModuleCreator> mainModuleCreators = 
            Categories.getAllElements(mainModuleCreatorCategory);
        this.moduleCreatorRepository.add(mainModuleCreators);

        Set<ModuleCreator> builtInModuleCreators = 
            Categories.getAllElements(builtInModuleCreatorCategory);
        this.moduleCreatorRepository.add(builtInModuleCreators);

        this.mainModuleCompatibilityModel =
            new ModuleCompatibilityModel(
                moduleCreatorRepository, mainModuleCreators);
        this.builtInModuleCompatibilityModel =
            new ModuleCompatibilityModel(
                moduleCreatorRepository, builtInModuleCreators);
    }
    
    /**
     * Returns the root node of the {@link Category} hierarchy that 
     * contains all available {@link ModuleCreator} instances.
     * 
     * @return The root {@link Category} with all available 
     * {@link ModuleCreator} instances.
     */
    public Category<ModuleCreator> getRootModuleCreatorCategory()
    {
        return mainModuleCreatorCategory;
    }
    
    /**
     * Returns the root node of the {@link Category} hierarchy that 
     * contains all built-in {@link ModuleCreator} instances.
     * 
     * @return The root {@link Category} with all built-in 
     * {@link ModuleCreator} instances.
     */
    public Category<ModuleCreator> getBuiltInModuleCreatorCategory()
    {
        return builtInModuleCreatorCategory;
    }
    
    /**
     * Returns the {@link Repository} containing all available 
     * {@link ModuleCreator} objects. This repository is mainly
     * intended for reverse lookups: For a given a {@link Module},
     * the {@link Module#getModuleInfo() ModuleInfo of this Module}
     * may be passed to this repository, in order to find the
     * {@link ModuleCreator} that created the {@link Module} instance.
     * 
     * @return The {@link Repository} of {@link ModuleCreator} objects
     */
    public Repository<ModuleInfo, ModuleCreator> getModuleCreatorRepository()
    {
        return moduleCreatorRepository;
    }
    
    /**
     * Returns the {@link ModuleCompatibilityModel} for the main modules
     * (except for the built-in ones)
     * 
     * @return The main {@link ModuleCompatibilityModel}
     */
    public ModuleCompatibilityModel getMainModuleCompatibilityModel()
    {
        return mainModuleCompatibilityModel;
    }
    
    /**
     * Returns the {@link ModuleCompatibilityModel} for the built-in modules
     * 
     * @return The {@link ModuleCompatibilityModel}
     */
    public ModuleCompatibilityModel getBuiltInModuleCompatibilityModel()
    {
        return builtInModuleCompatibilityModel;
    }
    

}
