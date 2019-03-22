/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.d;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;

/**
 * Implementation of a {@link Module} that only provides an Double that
 * is stored in its configuration
 */
public final class SelectableDoubleModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * The configuration
     */
    private final DoubleBean configuration;
    
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public SelectableDoubleModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        configuration = new DoubleBean();
    }
    
    @Override
    public DoubleBean getConfiguration()
    {
        return configuration;
    }
    
    @Override
    public void setConfiguration(Object object)
    {
        DoubleBean booleanBean = (DoubleBean)object;
        configuration.setValue(booleanBean.getValue());
    }

    @Override
    protected void process()
    {
        fireBeforeProcessing();
        fireAfterProcessing();
        forwardOutput(0, configuration.getValue());
    }

}