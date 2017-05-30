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
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link WekaClassifierModule} instances
 */
public final class WekaClassifierModuleCreator 
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
            .addInput(Instances.class, "Instances", "The input instances")
            .addOutput(Classifier.class, "Classifier", "The classifier")
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
    public WekaClassifierModuleCreator(String className)
    {
        super(createModuleInfo(className), className);
        this.className = className;
        setSupportedModuleViewTypes(ModuleViewTypes.CONFIGURATION_VIEW);
    }
    

    @Override
    public Module createModule()
    {
        return new WekaClassifierModule(getModuleInfo(), className);
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (moduleViewType.equals(ModuleViewTypes.CONFIGURATION_VIEW))
        {
            return new WekaModuleView(Classifier.class);
        }
        return null;
    }
    
    
}
