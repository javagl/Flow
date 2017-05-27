package de.javagl.flow.samples.gui01;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.samples.SampleModules;

@SuppressWarnings("javadoc")
public class SplitListModuleCreator extends AbstractModuleCreator
{
    public SplitListModuleCreator()
    {
        super(SampleModules.createSplitListModuleInfo());
    }

    @Override
    public Module createModule()
    {
        return SampleModules.createSplitListModule();
    }
}
