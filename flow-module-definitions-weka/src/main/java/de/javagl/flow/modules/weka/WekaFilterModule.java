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
import weka.core.Instances;
import weka.filters.Filter;

/**
 * Implementation of a {@link Module} that builds a Weka filter.
 */
final class WekaFilterModule 
    extends SimpleAbstractModule implements Module, WekaModule
{
    /**
     * The filter. 
     */
    private final Filter filter;

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
    public WekaFilterModule(ModuleInfo moduleInfo, String className)
    {
        super(moduleInfo);
        this.filter = (Filter) Classes.newInstanceOptional(className);
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
        return filter;
    }
    
    @Override
    protected void process()
    {
        Object input0 = obtainInput(0);
        Instances inputInstances = (Instances)input0;
        Instances outputInstances = null;
        
        fireBeforeProcessing();
        
        try
        {
            filter.setInputFormat(inputInstances);
            outputInstances = Filter.useFilter(inputInstances, filter);
        } 
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            fireAfterProcessing();
        }
        
        forwardOutput(0, outputInstances);
    }

}