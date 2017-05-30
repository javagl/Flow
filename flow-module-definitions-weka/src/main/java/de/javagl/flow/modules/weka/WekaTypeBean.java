/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A Java Bean containing a class name and options (for Weka)
 */
public final class WekaTypeBean
{
    /**
     * The class name
     */
    private String className = null;
    
    /**
     * The options
     */
    private String options = null;
    
    /**
     * The property change support
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * Default constructor
     */
    public WekaTypeBean()
    {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * Set the class name
     * 
     * @param className The className
     */
    public void setClassName(String className)
    {
        propertyChangeSupport.firePropertyChange(
            "className", this.className, this.className = className);        
    }

    /**
     * Return the className
     * 
     * @return The className
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Set the options
     * 
     * @param options The options
     */
    public void setOptions(String options)
    {
        propertyChangeSupport.firePropertyChange(
            "options", this.options, this.options = options);        
    }

    /**
     * Return the options
     * 
     * @return The options
     */
    public String getOptions()
    {
        return options;
    }
    
    /**
     * Add the given listener to be informed about property changes
     *
     * @param propertyChangeListener The listener to add
     */
    public final void addPropertyChangeListener(
        PropertyChangeListener propertyChangeListener)
    {
        propertyChangeSupport.addPropertyChangeListener(
            propertyChangeListener);
    }

    /**
     * Remove the given listener
     *
     * @param propertyChangeListener The listener to remove
     */
    public final void removePropertyChangeListener(
        PropertyChangeListener propertyChangeListener)
    {
        propertyChangeSupport.removePropertyChangeListener(
            propertyChangeListener);
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "["
            + "className=" + className + ","
            + "options=" + options + "]";
    }
    
}