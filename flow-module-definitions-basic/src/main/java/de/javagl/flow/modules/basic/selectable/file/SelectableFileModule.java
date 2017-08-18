/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.file;

import java.io.File;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.SimpleAbstractModule;
import de.javagl.flow.modules.basic.selectable.string.StringBean;

/**
 * Implementation of a {@link Module} that only provides a File based on
 * a file name that that is stored as a string in its configuration
 */
public final class SelectableFileModule 
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
    public SelectableFileModule(ModuleInfo moduleInfo)
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
        String fileName = configuration.getValue();
        File file = null;
        if (fileName != null)
        {
            file = new File(fileName);            
        }
        forwardOutput(0, file);
    }

}