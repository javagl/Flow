/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import de.javagl.flow.module.view.ModuleView;

/**
 * Implementation of a {@link Module} that receives an arbitrary object
 * and stores it (to be later shown in an associated {@link ModuleView} -
 * namely, in a {@link GuiConsoleModuleView})
 */
public final class GuiConsoleModule extends SimpleAbstractModule
{
    /**
     * The object
     */
    private Object object;
    
    /**
     * Creates a new instance
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public GuiConsoleModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }

    @Override
    protected void process()
    {
        object = obtainInput(0);
        fireBeforeProcessing();
        fireAfterProcessing();
    }
    
    /**
     * Returns the object
     * 
     * @return The object
     */
    Object getObject()
    {
        return object;
    }
    
}