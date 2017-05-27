package de.javagl.flow.samples.gui02;

import java.awt.GridLayout;
import java.util.List;

import de.javagl.flow.module.creation.AbstractModuleView;
import de.javagl.flow.module.view.ModuleView;

@SuppressWarnings("javadoc")
final class BarChartModuleView extends AbstractModuleView implements ModuleView
{
    private final CustomBarChartComponent customBarChartComponent;
    
    public BarChartModuleView()
    {
        customBarChartComponent = new CustomBarChartComponent();
        getComponent().setLayout(new GridLayout(1,1));
        getComponent().add(customBarChartComponent);
    }
    
    @Override
    protected void updateModuleView()
    {
        BarChartModule module = (BarChartModule)getModule();
        List<Integer> currentData = null;
        if (module != null)
        {
            currentData = module.getCurrentData();    
        }
        customBarChartComponent.update(currentData);
    }

}
