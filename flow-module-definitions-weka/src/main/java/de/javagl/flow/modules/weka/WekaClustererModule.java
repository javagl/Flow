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
import weka.clusterers.Clusterer;
import weka.core.Instances;

/**
 * Implementation of a {@link Module} that builds a Weka classifier.
 */
final class WekaClustererModule 
    extends SimpleAbstractModule implements Module, WekaModule
{
    /**
     * The classifier. This is instantiated only once, but built each
     * time that the module is executed.
     */
    private final Clusterer clusterer;

    /**
     * The configuration. A string bean containing the Weka options string
     */
    private final StringBean configuration;
    
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @param className The Weka class name
     */
    public WekaClustererModule(ModuleInfo moduleInfo, String className)
    {
        super(moduleInfo);
        this.clusterer = (Clusterer) Classes.newInstanceOptional(className);
        this.configuration = new StringBean();
    }
    
    @Override
    public StringBean getConfiguration()
    {
        return configuration;
    }
    
    @Override
    public void setConfiguration(Object configuration)
    {
        StringBean other = (StringBean)configuration;
        this.configuration.setValue(other.getValue());
    }
    
    @Override
    public Object getWekaObject()
    {
        return clusterer;
    }
    
    @Override
    protected void process()
    {
        Object input0 = obtainInput(0);
        Instances inputInstances = (Instances)input0;
        fireBeforeProcessing();
        
        try
        {
            clusterer.buildClusterer(inputInstances);
        } 
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            fireAfterProcessing();
        }
        
        forwardOutput(0, clusterer);
    }

}