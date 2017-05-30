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
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link ClusterEvaluationModule} instances
 */
public final class ClusterEvaluationModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Cluster Evaluation", "Creates a Weka ClusterEvaluation")
        .addInput(Clusterer.class, "Clusterer", "The input clusterer")
        .addInput(Instances.class, "Instances", "The input instances")
        .addOutput(ClusterEvaluation.class, 
            "ClusterEvaluation", "The Clusterer Evaluation")
        .build();

    /**
     * Default constructor
     */
    public ClusterEvaluationModuleCreator()
    {
        super(MODULE_INFO);
    }
    
    @Override
    public Module createModule()
    {
        return new ClusterEvaluationModule(getModuleInfo());
    }
}
