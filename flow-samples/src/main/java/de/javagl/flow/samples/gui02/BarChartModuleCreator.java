package de.javagl.flow.samples.gui02;

import java.util.Arrays;
import java.util.List;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewType;
import de.javagl.flow.module.view.ModuleViewTypes;
import de.javagl.types.Types;

@SuppressWarnings("javadoc")
public class BarChartModuleCreator extends AbstractModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Bar Chart", "Shows a bar chart, implemented with JFreeChart")
        .addInput(Types.create(List.class, Integer.class), 
            "Integer list", "The list of integers to create the bar chart for")
        .build();
    
    public BarChartModuleCreator()
    {
        super(MODULE_INFO, Arrays.asList(ModuleViewTypes.VISUALIZATION_VIEW));
    }

    @Override
    public Module createModule()
    {
        return new BarChartModule(MODULE_INFO);
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (!getSupportedModuleViewTypes().contains(moduleViewType))
        {
            return null;
        }
        return new BarChartModuleView();
    }
}
