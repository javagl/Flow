/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.j;

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
 * {@link SelectableLongModule} instances
 */
public final class SelectableLongModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Selectable Long", "Provides user-defined Longs")
        .addOutput(Long.class, "Long", "The provided Long")
        .build();

    /**
     * Default constructor
     */
    public SelectableLongModuleCreator()
    {
        super(MODULE_INFO);
        setSupportedModuleViewTypes(ModuleViewTypes.CONFIGURATION_VIEW);
    }
    
    @Override
    public Module createModule()
    {
        return new SelectableLongModule(getModuleInfo());
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (moduleViewType.equals(ModuleViewTypes.CONFIGURATION_VIEW))
        {
            return new SelectableLongModuleView();
        }
        return null;
    }
    

}
