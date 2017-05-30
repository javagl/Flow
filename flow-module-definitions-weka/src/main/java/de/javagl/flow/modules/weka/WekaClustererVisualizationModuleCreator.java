/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewType;
import de.javagl.flow.module.view.ModuleViewTypes;
import weka.clusterers.Clusterer;
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link WekaClustererVisualizationModule} instances
 */
public final class WekaClustererVisualizationModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * Create the {@link ModuleInfo} 
     * 
     * @return The {@link ModuleInfo}
     */
    private static ModuleInfo createModuleInfo()
    {
        return ModuleInfos.create("Clusterer Visualization", 
            "A module that can visualize a Weka Clusterer result")
            .addInput(Clusterer.class, "Clusterer", "The clusterer")
            .addInput(Instances.class, "Instances", "The input instances")
            .build();
    }
    
    /**
     * Default constructor
     */
    public WekaClustererVisualizationModuleCreator()
    {
        super(createModuleInfo());
        setSupportedModuleViewTypes(ModuleViewTypes.VISUALIZATION_VIEW);
    }
    

    @Override
    public Module createModule()
    {
        return new WekaClustererVisualizationModule(getModuleInfo());
    }
    
    @Override
    public ModuleView createModuleView(ModuleViewType moduleViewType)
    {
        if (moduleViewType.equals(ModuleViewTypes.VISUALIZATION_VIEW))
        {
            return new WekaClustererVisualizationModuleView();
        }
        return null;
    }
    
    
}
