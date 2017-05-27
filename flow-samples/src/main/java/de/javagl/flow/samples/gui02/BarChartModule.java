package de.javagl.flow.samples.gui02;

import java.util.List;

import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;

@SuppressWarnings("javadoc")
final class BarChartModule extends SimpleAbstractModule
{
    private List<Integer> currentData;
    
    protected BarChartModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process()
    {
        Object input = obtainInput(0);
        fireBeforeProcessing();
        currentData = (List<Integer>) input;
        fireAfterProcessing();
    }
    
    List<Integer> getCurrentData()
    {
        return currentData;
    }
}
