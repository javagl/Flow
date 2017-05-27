/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;

/**
 * Implementation of a {@link Module} that prints its input to the console
 */
public final class ConsoleModule extends SimpleAbstractModule
{
    /**
     * Creates a new instance
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public ConsoleModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }

    @Override
    protected void process()
    {
        System.out.println(obtainInput(0));
    }
    
}