/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic;

import de.javagl.flow.plugin.AbstractModuleCreatorSource;
import de.javagl.flow.plugin.ModuleCreatorSource;

/**
 * Implementation of a {@link ModuleCreatorSource} for the basic modules
 */
public class BasicModuleCreatorSource 
    extends AbstractModuleCreatorSource 
    implements ModuleCreatorSource
{
    /**
     * Default constructor
     */
    public BasicModuleCreatorSource()
    {
        super("Basic");
    }

    @Override
    protected void addModuleCreators()
    {
        add(new ConsoleModuleCreator());
        add(new GuiConsoleModuleCreator());
    }
}
