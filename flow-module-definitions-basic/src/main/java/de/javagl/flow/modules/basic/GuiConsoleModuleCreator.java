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
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewType;
import de.javagl.flow.module.view.ModuleViewTypes;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link GuiConsoleModule} instances
 */
public final class GuiConsoleModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "GUI Console", "Writes all objects into a GUI console")
        .addInput(Object.class, "Input", "The object to write")
        .build();
    
    /**
     * Default constructor
     */
    public GuiConsoleModuleCreator()
    {
        super(MODULE_INFO);
        setSupportedModuleViewTypes(ModuleViewTypes.VISUALIZATION_VIEW);
    }

    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (moduleViewType.equals(ModuleViewTypes.VISUALIZATION_VIEW))
        {
            return new GuiConsoleModuleView();
        }
        return null;
    }
    
    @Override
    public Module createModule()
    {
        return new GuiConsoleModule(getModuleInfo());
    }
}
