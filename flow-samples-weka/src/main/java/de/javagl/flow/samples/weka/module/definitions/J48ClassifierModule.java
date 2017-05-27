/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.samples.weka.module.definitions;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import de.javagl.reflection.Classes;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Implementation of a {@link Module} that builds a Weka J48 classifier.
 */
public final class J48ClassifierModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public J48ClassifierModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }
    
    @Override
    protected void process()
    {
        Object input0 = obtainInput(0);
        Instances inputInstances = (Instances)input0;
        Classifier outputClassifier = null;
        
        fireBeforeProcessing();
        
        try
        {
            String name = "weka.classifiers.trees.J48";
            outputClassifier = 
                (Classifier) Classes.newInstanceOptional(name);
            outputClassifier.buildClassifier(inputInstances);
        } 
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            fireAfterProcessing();
        }
        
        forwardOutput(0, outputClassifier);
    }

}