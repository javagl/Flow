/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.samples.weka.module.definitions;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.AbstractModuleCreator;
import de.javagl.flow.module.creation.ModuleCreator;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Implementation of a {@link ModuleCreator} that creates 
 * {@link EvaluationModule} instances
 */
public final class EvaluationModuleCreator 
    extends AbstractModuleCreator implements ModuleCreator
{
    /**
     * The {@link ModuleInfo}
     */
    private static final ModuleInfo MODULE_INFO = ModuleInfos.create(
        "Evaluation", "Creates a Weka classifier Evaluation")
        .addInput(Classifier.class, "Classifier", "The input classifier")
        .addInput(Instances.class, "Instances", "The input instances")
        .addOutput(Evaluation.class, "Evaluation", "The Evaluation")
        .build();

    /**
     * Default constructor
     */
    public EvaluationModuleCreator()
    {
        super(MODULE_INFO);
    }
    
    @Override
    public Module createModule()
    {
        return new EvaluationModule(getModuleInfo());
    }
}
