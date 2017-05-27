/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.module.creation.ModuleCreator;

/**
 * Implementation of a {@link ModuleCreator} that creates {@link ConsoleModule}
 * instances
 */
public final class ConsoleModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = 
        ModuleInfos.create("Console", "Writes all objects to the console")
            .addInput(Object.class, "Input", "The object to write")
            .build();

    /**
     * Default constructor
     */
    public ConsoleModuleCreator()
    {
        super(MODULE_INFO);
    }

    @Override
    public Module createModule()
    {
        return new ConsoleModule(getModuleInfo());
    }
}
