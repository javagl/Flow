/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.string;

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
 * {@link SelectableStringModule} instances
 */
public final class SelectableStringModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Selectable String", "Provides user-defined Strings")
        .addOutput(String.class, "String", "The provided String")
        .build();

    /**
     * Default constructor
     */
    public SelectableStringModuleCreator()
    {
        super(MODULE_INFO);
        setSupportedModuleViewTypes(ModuleViewTypes.CONFIGURATION_VIEW);
    }
    
    @Override
    public Module createModule()
    {
        return new SelectableStringModule(getModuleInfo());
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (moduleViewType.equals(ModuleViewTypes.CONFIGURATION_VIEW))
        {
            return new SelectableStringModuleView();
        }
        return null;
    }
    

}
