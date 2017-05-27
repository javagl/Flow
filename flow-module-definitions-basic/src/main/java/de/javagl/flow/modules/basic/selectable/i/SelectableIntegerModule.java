/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.i;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;

/**
 * Implementation of a {@link Module} that only provides an Integer that
 * is stored in its configuration
 */
public final class SelectableIntegerModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * The configuration
     */
    private final IntegerBean configuration;
    
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public SelectableIntegerModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        configuration = new IntegerBean();
    }
    
    @Override
    public IntegerBean getConfiguration()
    {
        return configuration;
    }
    
    @Override
    public void setConfiguration(Object object)
    {
        IntegerBean booleanBean = (IntegerBean)object;
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