/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.plugin.AbstractModuleCreatorSource;
import de.javagl.flow.plugin.ModuleCreatorSource;
import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.filters.Filter;
import weka.gui.GenericObjectEditor;

/**
 * Implementation of a {@link ModuleCreatorSource} for Weka modules
 */
public class WekaModuleCreatorSource 
    extends AbstractModuleCreatorSource 
    implements ModuleCreatorSource
{
    static
    {
        GenericObjectEditor.registerEditors();
    }
    
    /**
     * Default constructor
     */
    public WekaModuleCreatorSource()
    {
        super("Weka");
    }

    @Override
    protected void addModuleCreators()
    {
        get("Load").add(new ArffFileLoaderModuleCreator());
        get("Load").add(new IrisLoaderModuleCreator());

        get("Classifiers").add(new GenericWekaClassifierModuleCreator());
        get("Classifiers").add(new EvaluationModuleCreator());
        WekaCategoriesGenerator.generateCategories(
            Classifier.class, get(), WekaClassifierModuleCreator::new);

        WekaCategoriesGenerator.generateCategories(
            Filter.class, get(), WekaFilterModuleCreator::new);
        
        get("Clusterers").add(new ClusterEvaluationModuleCreator());
        WekaCategoriesGenerator.generateCategories(
            Clusterer.class, get(), WekaClustererModuleCreator::new);
        
        get("Visualize").add(new WekaClustererVisualizationModuleCreator());
        
        
    }
}
