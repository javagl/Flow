/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable;

import de.javagl.flow.modules.basic.selectable.f.SelectableFloatModuleCreator;
import de.javagl.flow.modules.basic.selectable.file.SelectableFileModuleCreator;
import de.javagl.flow.modules.basic.selectable.i.SelectableIntegerModuleCreator;
import de.javagl.flow.modules.basic.selectable.string.SelectableStringModuleCreator;
import de.javagl.flow.modules.basic.selectable.z.SelectableBooleanModuleCreator;
import de.javagl.flow.plugin.AbstractModuleCreatorSource;
import de.javagl.flow.plugin.ModuleCreatorSource;

/**
 * Implementation of a {@link ModuleCreatorSource} for the basic selection 
 * modules
 */
public class SelectableModuleCreatorSource 
    extends AbstractModuleCreatorSource 
    implements ModuleCreatorSource
{
    /**
     * Default constructor
     */
    public SelectableModuleCreatorSource()
    {
        super("Basic");
    }

    @Override
    protected void addModuleCreators()
    {
        get("Selectable").add(new SelectableStringModuleCreator());
        get("Selectable").add(new SelectableBooleanModuleCreator());
        get("Selectable").add(new SelectableIntegerModuleCreator());
        get("Selectable").add(new SelectableFloatModuleCreator());
        get("Selectable").add(new SelectableFileModuleCreator());
    }
}
