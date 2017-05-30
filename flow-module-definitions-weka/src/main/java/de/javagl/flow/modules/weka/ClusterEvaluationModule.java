/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Instances;

/**
 * Implementation of a {@link Module} that builds a Weka Cluster Evaluation 
 */
final class ClusterEvaluationModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public ClusterEvaluationModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }
    
    @Override
    protected void process()
    {
        Object input0 = obtainInput(0);
        Object input1 = obtainInput(1);
        Clusterer inputClusterer = (Clusterer)input0;
        Instances inputInstances = (Instances)input1;
        ClusterEvaluation outputClusterEvaluation = null;
        
        fireBeforeProcessing();
        
        try
        {
            outputClusterEvaluation = new ClusterEvaluation();
            outputClusterEvaluation.setClusterer(inputClusterer);
            outputClusterEvaluation.evaluateClusterer(inputInstances);
        } 
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            fireAfterProcessing();
        }
        
        forwardOutput(0, outputClusterEvaluation);
    }

}