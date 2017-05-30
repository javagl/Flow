/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Implementation of a {@link Module} that builds a Weka Evaluation 
 */
final class EvaluationModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public EvaluationModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }
    
    @Override
    protected void process()
    {
        Object input0 = obtainInput(0);
        Object input1 = obtainInput(1);
        Classifier inputClassifier = (Classifier)input0;
        Instances inputInstances = (Instances)input1;
        Evaluation outputEvaluation = null;
        
        fireBeforeProcessing();
        
        try
        {
            outputEvaluation = new Evaluation(inputInstances);
            outputEvaluation.crossValidateModel(
                inputClassifier, inputInstances, 10, 
                inputInstances.getRandomNumberGenerator(1));
        } 
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            fireAfterProcessing();
        }
        
        forwardOutput(0, outputEvaluation);
    }

}