/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.samples.weka.module.definitions;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.module.creation.ModuleCreator;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link J48ClassifierModule} instances
 */
public final class J48ClassifierModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "J48", "Creates a J48 classifier")
        .addInput(Instances.class, "Input", "The input instances")
        .addOutput(Classifier.class, "Output", "The classifier")
        .build();

    /**
     * Default constructor
     */
    public J48ClassifierModuleCreator()
    {
        super(MODULE_INFO);
    }
    
    @Override
    public Module createModule()
    {
        return new J48ClassifierModule(getModuleInfo());
    }
}
