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
 * {@link RandomizeFilterModule} instances
 */
public final class RandomizeFilterModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Randomize", "Applies the Weka 'Randomize' filter")
        .addInput(Instances.class, "Input", "The input instances")
        .addOutput(Instances.class, "Output", "The output instances")
        .build();

    /**
     * Default constructor
     */
    public RandomizeFilterModuleCreator()
    {
        super(MODULE_INFO);
    }
    
    @Override
    public Module createModule()
    {
        return new RandomizeFilterModule(getModuleInfo());
    }
}
