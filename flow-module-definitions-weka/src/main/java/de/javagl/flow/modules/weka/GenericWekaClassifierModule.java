/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import de.javagl.reflection.Classes;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Implementation of a {@link Module} that builds a Weka object.
 */
final class GenericWekaClassifierModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * The {@link #getConfiguration() configuration}
     */
    private final WekaTypeBean configuration;
    
    /**
     * Default constructor
     *  
     * @param moduleInfo The {@link ModuleInfo}
     */
    public GenericWekaClassifierModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        this.configuration = new WekaTypeBean();
    }
    
    @Override
    public Object getConfiguration()
    {
        return configuration;
    }
    
    @Override
    public void setConfiguration(Object configuration)
    {
        WekaTypeBean other = (WekaTypeBean)configuration;
        this.configuration.setClassName(other.getClassName());
        this.configuration.setOptions(other.getOptions());
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
            String className = configuration.getClassName();
            outputClassifier = 
                (Classifier) Classes.newInstanceUnchecked(className);
            
            String options = configuration.getOptions();
            WekaOptions.trySetOptions(outputClassifier, options);
            
            inputInstances.setClassIndex(inputInstances.numAttributes() - 1);
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