package de.javagl.flow.samples.gui01;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.samples.SampleModules;

@SuppressWarnings("javadoc")
public class MergeListsModuleCreator extends AbstractModuleCreator
{
    public MergeListsModuleCreator()
    {
        super(SampleModules.createMergeListsModuleInfo());
    }

    @Override
    public Module createModule()
    {
        return SampleModules.createMergeListsModule();
    }
}
