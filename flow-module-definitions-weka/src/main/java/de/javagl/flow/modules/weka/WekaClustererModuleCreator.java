/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewType;
import de.javagl.flow.module.view.ModuleViewTypes;
import weka.clusterers.Clusterer;
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link WekaClustererModule} instances
 */
public final class WekaClustererModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * Create the {@link ModuleInfo} for the given class name
     * 
     * @param className The class name
     * @return The {@link ModuleInfo}
     */
    private static ModuleInfo createModuleInfo(String className)
    {
        String name = 
            WekaCategoriesGenerator.getUnqualifiedClassName(className);
        String description = 
            WekaCategoriesGenerator.getDescriptionHtml(className);
        return ModuleInfos.create(name, description)
            .addInput(Instances.class, "Input", "The input instances")
            .addOutput(Clusterer.class, "Clusterer", "The clusterer")
            .build();
    }
    
    /**
     * The class name
     */
    private final String className;
    
    /**
     * Default constructor
     * 
     * @param className The class name
     */
    public WekaClustererModuleCreator(String className)
    {
        super(createModuleInfo(className), className);
        this.className = className;
        setSupportedModuleViewTypes(ModuleViewTypes.CONFIGURATION_VIEW);
    }
    

    @Override
    public Module createModule()
    {
        return new WekaClustererModule(getModuleInfo(), className);
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (moduleViewType.equals(ModuleViewTypes.CONFIGURATION_VIEW))
        {
            return new WekaModuleView(Clusterer.class);
        }
        return null;
    }
    
    
}
