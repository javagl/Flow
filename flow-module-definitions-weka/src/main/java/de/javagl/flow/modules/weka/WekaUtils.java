/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Weka utilities
 */
class WekaUtils
{
    /**
     * Creates a deep copy of the given instances.
     * 
     * @param instances The instances
     * @return The deep copy
     */
    static Instances deepCopy(Instances instances)
    {
        // TODO If this is supposed to be the only and right way to create
        // a deep copy of an Instances object, then somewhere something
        // went horribly wrong.... 
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        for (int i = 0; i < instances.numAttributes(); i++)
        {
            Attribute attribute = instances.attribute(i);
            attributes.add((Attribute) attribute.copy());
        }
        Instances copy = new Instances(instances.relationName(), 
            attributes, instances.numInstances());
        for (int i = 0; i < instances.numInstances(); i++)
        {
            Instance instance = instances.instance(i);
            copy.add((Instance) instance.copy());
        }
        return copy;
    }

    
    /**
     * Private constructor to prevent instantiation
     */
    private WekaUtils()
    {
        // Private constructor to prevent instantiation
    }
}
