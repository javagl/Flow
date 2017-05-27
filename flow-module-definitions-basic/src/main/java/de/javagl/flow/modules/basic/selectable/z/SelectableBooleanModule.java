/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.z;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;

/**
 * Implementation of a {@link Module} that only provides a Boolean that
 * is stored in its configuration
 */
public final class SelectableBooleanModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * The configuration
     */
    private final BooleanBean configuration;
    
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public SelectableBooleanModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        configuration = new BooleanBean();
    }
    
    @Override
    public BooleanBean getConfiguration()
    {
        return configuration;
    }
    
    @Override
    public void setConfiguration(Object object)
    {
        BooleanBean booleanBean = (BooleanBean)object;
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