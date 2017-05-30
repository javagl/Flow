/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A {@link Module} that serves as the basis for a
 * {@link WekaClustererVisualizationModuleView}
 */
final class WekaClustererVisualizationModule extends SimpleAbstractModule
{
    /**
     * The clusterer
     */
    private Clusterer clusterer;
    
    /**
     * The clustered instances
     */
    private Instances clusteredInstances;
    
    /**
     * Default constructor
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    protected WekaClustererVisualizationModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }

    @Override
    protected void process()
    {
        Object input0 = obtainInput(0);
        Object input1= obtainInput(1);
        
        fireBeforeProcessing();
        
        try
        {
            Clusterer clusterer = (Clusterer) input0;
            this.clusterer = clusterer;
            
            Instances inputInstances = (Instances) input1;
            
            clusteredInstances = WekaUtils.deepCopy(inputInstances);
            int oldClassIndex = clusteredInstances.classIndex();
            if (oldClassIndex != -1)
            {
                clusteredInstances.deleteAttributeAt(oldClassIndex);
            }
            int newClassIndex = clusteredInstances.numAttributes() - 1;
            clusteredInstances.setClassIndex(newClassIndex);
            for (int i=0; i<clusteredInstances.size(); i++)
            {
                Instance instance = clusteredInstances.get(i);
                int cluster = clusterer.clusterInstance(instance);
                instance.setValue(newClassIndex, cluster);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            fireAfterProcessing();
        }
    }
    
    /**
     * Returns the clusterer. May be <code>null</code>
     * 
     * @return The clusterer
     */
    Clusterer getClusterer()
    {
        return clusterer;
    }
    
    /**
     * Returns the clustered instances. May be <code>null</code>.
     * 
     * @return The instances
     */
    Instances getClusteredInstances()
    {
        return clusteredInstances;
    }
}
