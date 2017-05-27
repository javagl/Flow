/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.samples.weka.module.definitions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import weka.core.Instances;

/**
 * Implementation of a {@link Module} loads an ARFF file and returns
 * the resulting instances.
 */
public final class InstancesLoaderModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public InstancesLoaderModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }
    
    @Override
    protected void process()
    {
        Object input0 = obtainInput(0);
        String fileName = String.valueOf(input0);
        
        fireBeforeProcessing();
        
        Instances instances = null;
        try
        {
            instances = new Instances(new BufferedReader(
                new FileReader(fileName)));
            instances.setClassIndex(instances.numAttributes() - 1);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            fireAfterProcessing();
        }
        
        forwardOutput(0, instances);
    }

}