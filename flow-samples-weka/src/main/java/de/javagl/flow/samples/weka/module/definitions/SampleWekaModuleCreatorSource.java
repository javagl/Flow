/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.samples.weka.module.definitions;

import de.javagl.flow.plugin.AbstractModuleCreatorSource;
import de.javagl.flow.plugin.ModuleCreatorSource;

/**
 * Implementation of a {@link ModuleCreatorSource} for the sample Weka modules
 */
public class SampleWekaModuleCreatorSource 
    extends AbstractModuleCreatorSource 
    implements ModuleCreatorSource
{
    /**
     * Default constructor
     */
    public SampleWekaModuleCreatorSource()
    {
        super("Weka Samples");
    }

    @Override
    protected void addModuleCreators()
    {
        get("Load").add(new InstancesLoaderModuleCreator());
        get("Filter").add(new RandomizeFilterModuleCreator());
        get("Classify").add(new J48ClassifierModuleCreator());
        get("Evaluate").add(new EvaluationModuleCreator());
    }
}
