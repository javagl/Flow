/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.string;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;

/**
 * Implementation of a {@link Module} that only provides a String that
 * is stored in its configuration
 */
public final class SelectableStringModule 
    extends SimpleAbstractModule implements Module
{
    /**
     * The configuration
     */
    private final StringBean configuration;
    
    /**
     * Default constructor 
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public SelectableStringModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        configuration = new StringBean();
    }
    
    @Override
    public StringBean getConfiguration()
    {
        return configuration;
    }
    
    @Override
    public void setConfiguration(Object object)
    {
        StringBean booleanBean = (StringBean)object;
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