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
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link ArffFileLoaderModule} instances
 */
public final class ArffFileLoaderModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "ARFF loader", "Loads instances from an ARFF file")
        .addInput(String.class, "File name", 
            "The name of the ARFF file to load")
        .addOutput(Instances.class, "Instances", 
            "The instances that have been loaded from the ARFF file")
        .build();

    /**
     * Default constructor
     */
    public ArffFileLoaderModuleCreator()
    {
        super(MODULE_INFO);
    }
    
    @Override
    public Module createModule()
    {
        return new ArffFileLoaderModule(getModuleInfo());
    }
}
