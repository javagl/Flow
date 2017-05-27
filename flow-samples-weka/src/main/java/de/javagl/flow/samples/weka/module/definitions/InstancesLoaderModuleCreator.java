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
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link InstancesLoaderModule} instances
 */
public final class InstancesLoaderModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Load instances", "Loads instances from an ARFF file")
        .addInput(String.class, "File name", 
            "The name of the ARFF file to load")
        .addOutput(Instances.class, "Instances", 
            "The instances that have been loaded from the ARFF file")
        .build();

    /**
     * Default constructor
     */
    public InstancesLoaderModuleCreator()
    {
        super(MODULE_INFO);
    }
    
    @Override
    public Module createModule()
    {
        return new InstancesLoaderModule(getModuleInfo());
    }
}
