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
 * {@link GenericWekaClassifierModule} instances
 */
public final class GenericWekaClassifierModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Generic Classifier", "Creates and initializes a Weka classifier")
        .addInput(Instances.class, "Instances", "The input instances")
        .addOutput(Classifier.class, "Classifier", "The classifier")
        .build();

    /**
     * Default constructor
     */
    public GenericWekaClassifierModuleCreator()
    {
        super(MODULE_INFO);
        setSupportedModuleViewTypes(ModuleViewTypes.CONFIGURATION_VIEW);
    }
    
    @Override
    public Module createModule()
    {
        return new GenericWekaClassifierModule(getModuleInfo());
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (moduleViewType.equals(ModuleViewTypes.CONFIGURATION_VIEW))
        {
            return new WekaTypeModuleView(weka.classifiers.Classifier.class);
        }
        return null;
    }
    
    
}
