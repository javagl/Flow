/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import weka.core.Instances;

/**
 * Implementation of a {@link Module} that provides the iris data set.
 */
final class IrisLoaderModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public IrisLoaderModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
    }
    
    @Override
    protected void process()
    {
        fireBeforeProcessing();
        
        Instances instances = null;
        try (Reader reader = new BufferedReader(new InputStreamReader(
            IrisLoaderModule.class.getResourceAsStream("/data/iris.arff"))))
        {
            instances = new Instances(reader);
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