/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.j;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;

/**
 * Implementation of a {@link Module} that only provides an Long that
 * is stored in its configuration
 */
public final class SelectableLongModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * The configuration
     */
    private final LongBean configuration;
    
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public SelectableLongModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        configuration = new LongBean();
    }
    
    @Override
    public LongBean getConfiguration()
    {
        return configuration;
    }
    
    @Override
    public void setConfiguration(Object object)
    {
        LongBean booleanBean = (LongBean)object;
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