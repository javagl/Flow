/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import de.javagl.flow.module.Module;

/**
 * Internal interface for Weka {@link Module} implementations
 */
interface WekaModule extends Module
{
    /**
     * Returns the Weka object. This may, for example, be a Weka Classifier
     * or Weka Filter object.
     * 
     * @return The weka object
     */
    Object getWekaObject();
    
    @Override
    StringBean getConfiguration();
}
