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
import weka.core.Instances;
import weka.filters.Filter;

/**
 * Implementation of a {@link Module} that executes a Weka Filter
 */
public final class RandomizeFilterModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * The filter
     */
    private final Filter filter;
    
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public RandomizeFilterModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        
        String name = "weka.filters.unsupervised.instance.Randomize";
        this.filter = (Filter)Classes.newInstanceOptional(name);
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